package com.gotechmedia.app.domain.usecase.auth

import com.gotechmedia.app.core.auth.AuthState
import com.gotechmedia.app.core.auth.UserRole
import com.gotechmedia.app.core.utils.Resource
import com.gotechmedia.app.domain.model.AgencyUser
import com.gotechmedia.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class GetAuthStateUseCase(private val repository: AuthRepository) {
    operator fun invoke(): StateFlow<AuthState> = repository.authState
    val currentUser: AgencyUser? get() = repository.currentUser
}

class SignInWithEmailUseCase(private val repository: AuthRepository) {
    operator fun invoke(email: String, password: String): Flow<Resource<AgencyUser>> =
        repository.signInWithEmail(email, password)
}

class SignUpWithEmailUseCase(private val repository: AuthRepository) {
    operator fun invoke(
        email: String,
        password: String,
        displayName: String,
        company: String,
        role: UserRole = UserRole.CLIENT
    ): Flow<Resource<AgencyUser>> =
        repository.signUpWithEmail(email, password, displayName, company, role)
}

class SignInWithGoogleUseCase(private val repository: AuthRepository) {
    operator fun invoke(idToken: String): Flow<Resource<AgencyUser>> =
        repository.signInWithGoogle(idToken)
}

class SendPasswordResetUseCase(private val repository: AuthRepository) {
    operator fun invoke(email: String): Flow<Resource<Unit>> =
        repository.sendPasswordResetEmail(email)
}

class SignOutUseCase(private val repository: AuthRepository) {
    operator fun invoke(): Flow<Resource<Unit>> = repository.signOut()
}
