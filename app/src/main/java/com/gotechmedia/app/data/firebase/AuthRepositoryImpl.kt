package com.gotechmedia.app.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.gotechmedia.app.core.auth.AuthState
import com.gotechmedia.app.core.auth.UserRole
import com.gotechmedia.app.core.utils.Resource
import com.gotechmedia.app.domain.model.AgencyUser
import com.gotechmedia.app.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Production implementation of AuthRepository backed by Firebase Authentication and Cloud Firestore.
 */
class AuthRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AuthRepository {

    private val repositoryScope = CoroutineScope(SupervisorJob() + ioDispatcher)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    @Volatile
    private var cachedUser: AgencyUser? = null

    override val currentUser: AgencyUser?
        get() = cachedUser

    init {
        // Observe Firebase Auth changes and fetch user document
        auth.addAuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser == null) {
                cachedUser = null
                _authState.value = AuthState.Unauthenticated
            } else {
                repositoryScope.launch {
                    try {
                        val agencyUser = fetchOrCreateFirestoreUser(firebaseUser)
                        cachedUser = agencyUser
                        _authState.value = AuthState.Authenticated(agencyUser)
                    } catch (e: Exception) {
                        // If offline or network issue, fallback to basic Firebase user info
                        val fallback = mapFirebaseUser(firebaseUser)
                        cachedUser = fallback
                        _authState.value = AuthState.Authenticated(fallback)
                    }
                }
            }
        }
    }

    override fun signInWithEmail(email: String, password: String): Flow<Resource<AgencyUser>> = flow {
        emit(Resource.Loading)
        try {
            val authResult = auth.signInWithEmailAndPassword(email.trim(), password).await()
            val firebaseUser = authResult.user ?: throw IllegalStateException("Firebase user was null after sign-in")
            val agencyUser = fetchOrCreateFirestoreUser(firebaseUser)
            cachedUser = agencyUser
            _authState.value = AuthState.Authenticated(agencyUser)
            emit(Resource.Success(agencyUser))
        } catch (e: FirebaseAuthInvalidUserException) {
            emit(Resource.Error("No enterprise account found with this email address."))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            emit(Resource.Error("Invalid credentials. Please verify your email and password."))
        } catch (e: FirebaseAuthException) {
            emit(Resource.Error(e.localizedMessage ?: "Authentication failed."))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred."))
        }
    }.flowOn(ioDispatcher)

    override fun signUpWithEmail(
        email: String,
        password: String,
        displayName: String,
        company: String,
        role: UserRole
    ): Flow<Resource<AgencyUser>> = flow {
        emit(Resource.Loading)
        try {
            val authResult = auth.createUserWithEmailAndPassword(email.trim(), password).await()
            val firebaseUser = authResult.user ?: throw IllegalStateException("User creation failed")

            // Update Firebase Auth profile
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName.trim())
                .build()
            firebaseUser.updateProfile(profileUpdates).await()

            // Construct new user entity
            val now = System.currentTimeMillis()
            val agencyUser = AgencyUser(
                id = firebaseUser.uid,
                email = firebaseUser.email ?: email.trim(),
                displayName = displayName.trim(),
                photoUrl = firebaseUser.photoUrl?.toString(),
                role = role,
                company = company.trim(),
                createdAtEpoch = now,
                updatedAtEpoch = now
            )

            // Save to Firestore `users/{uid}`
            firestore.collection("users").document(firebaseUser.uid)
                .set(agencyUserToMap(agencyUser), SetOptions.merge())
                .await()

            // Also create initial client profile in `clients/{uid}` if client role
            if (role == UserRole.CLIENT) {
                val clientData = hashMapOf(
                    "id" to firebaseUser.uid,
                    "userId" to firebaseUser.uid,
                    "companyName" to company.trim(),
                    "contactEmail" to (firebaseUser.email ?: email.trim()),
                    "tier" to "Enterprise",
                    "status" to "ACTIVE",
                    "createdAtEpoch" to now
                )
                firestore.collection("clients").document(firebaseUser.uid)
                    .set(clientData, SetOptions.merge())
                    .await()
            }

            cachedUser = agencyUser
            _authState.value = AuthState.Authenticated(agencyUser)
            emit(Resource.Success(agencyUser))
        } catch (e: FirebaseAuthWeakPasswordException) {
            emit(Resource.Error("Password must be at least 6 characters long."))
        } catch (e: FirebaseAuthUserCollisionException) {
            emit(Resource.Error("An account with this email address already exists."))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            emit(Resource.Error("Invalid email address format."))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to create account."))
        }
    }.flowOn(ioDispatcher)

    override fun signInWithGoogle(idToken: String): Flow<Resource<AgencyUser>> = flow {
        emit(Resource.Loading)
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user ?: throw IllegalStateException("Google sign in returned no user")

            val agencyUser = fetchOrCreateFirestoreUser(firebaseUser)
            cachedUser = agencyUser
            _authState.value = AuthState.Authenticated(agencyUser)
            emit(Resource.Success(agencyUser))
        } catch (e: FirebaseAuthException) {
            emit(Resource.Error(e.localizedMessage ?: "Google Sign-In authentication failed."))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Google Sign-In could not be completed."))
        }
    }.flowOn(ioDispatcher)

    override fun sendPasswordResetEmail(email: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            auth.sendPasswordResetEmail(email.trim()).await()
            emit(Resource.Success(Unit))
        } catch (e: FirebaseAuthInvalidUserException) {
            emit(Resource.Error("No registered partner account found with this email."))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            emit(Resource.Error("Please enter a valid email address."))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to dispatch password recovery email."))
        }
    }.flowOn(ioDispatcher)

    override fun signOut(): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            auth.signOut()
            cachedUser = null
            _authState.value = AuthState.Unauthenticated
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Sign out encountered an error."))
        }
    }.flowOn(ioDispatcher)

    override fun reloadUser(): Flow<Resource<AgencyUser>> = flow {
        emit(Resource.Loading)
        try {
            val user = auth.currentUser ?: throw IllegalStateException("No active user session")
            user.reload().await()
            val updatedUser = fetchOrCreateFirestoreUser(user)
            cachedUser = updatedUser
            _authState.value = AuthState.Authenticated(updatedUser)
            emit(Resource.Success(updatedUser))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to refresh user credentials."))
        }
    }.flowOn(ioDispatcher)

    private suspend fun fetchOrCreateFirestoreUser(firebaseUser: FirebaseUser): AgencyUser {
        val userDoc = firestore.collection("users").document(firebaseUser.uid).get().await()
        return if (userDoc.exists()) {
            mapDocumentToAgencyUser(userDoc.id, userDoc.data ?: emptyMap())
        } else {
            val now = System.currentTimeMillis()
            val newUser = AgencyUser(
                id = firebaseUser.uid,
                email = firebaseUser.email.orEmpty(),
                displayName = firebaseUser.displayName ?: firebaseUser.email?.substringBefore("@").orEmpty(),
                photoUrl = firebaseUser.photoUrl?.toString(),
                role = UserRole.CLIENT,
                company = "Enterprise Partner",
                createdAtEpoch = now,
                updatedAtEpoch = now
            )
            firestore.collection("users").document(firebaseUser.uid)
                .set(agencyUserToMap(newUser), SetOptions.merge())
                .await()
            newUser
        }
    }

    private fun mapFirebaseUser(user: FirebaseUser): AgencyUser {
        return AgencyUser(
            id = user.uid,
            email = user.email.orEmpty(),
            displayName = user.displayName ?: user.email?.substringBefore("@").orEmpty(),
            photoUrl = user.photoUrl?.toString(),
            role = UserRole.CLIENT,
            company = "Enterprise Client"
        )
    }

    private fun mapDocumentToAgencyUser(id: String, map: Map<String, Any?>): AgencyUser {
        val roleString = map["role"] as? String
        return AgencyUser(
            id = id,
            email = map["email"] as? String ?: "",
            displayName = map["displayName"] as? String ?: "",
            photoUrl = map["photoUrl"] as? String,
            role = UserRole.fromString(roleString),
            company = map["company"] as? String ?: "",
            phone = map["phone"] as? String ?: "",
            fcmToken = map["fcmToken"] as? String,
            createdAtEpoch = (map["createdAtEpoch"] as? Number)?.toLong() ?: System.currentTimeMillis(),
            updatedAtEpoch = (map["updatedAtEpoch"] as? Number)?.toLong() ?: System.currentTimeMillis()
        )
    }

    private fun agencyUserToMap(user: AgencyUser): Map<String, Any?> {
        return mapOf(
            "id" to user.id,
            "email" to user.email,
            "displayName" to user.displayName,
            "photoUrl" to user.photoUrl,
            "role" to user.role.name,
            "company" to user.company,
            "phone" to user.phone,
            "fcmToken" to user.fcmToken,
            "createdAtEpoch" to user.createdAtEpoch,
            "updatedAtEpoch" to user.updatedAtEpoch
        )
    }
}
