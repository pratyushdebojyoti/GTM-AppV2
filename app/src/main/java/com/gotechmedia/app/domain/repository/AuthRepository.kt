package com.gotechmedia.app.domain.repository

import com.gotechmedia.app.core.auth.AuthState
import com.gotechmedia.app.core.auth.UserRole
import com.gotechmedia.app.core.utils.Resource
import com.gotechmedia.app.domain.model.AgencyUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Authentication Repository abstraction.
 * Provides pure domain methods for email/password, Google Sign-In, password reset,
 * and user session management without exposing Firebase implementation details to the UI layer.
 */
interface AuthRepository {

    /**
     * Observable stream of current authentication state.
     */
    val authState: StateFlow<AuthState>

    /**
     * Synchronous access to current authenticated user or null.
     */
    val currentUser: AgencyUser?

    /**
     * Authenticate with email and password credentials.
     */
    fun signInWithEmail(email: String, password: String): Flow<Resource<AgencyUser>>

    /**
     * Register a new client or user account.
     */
    fun signUpWithEmail(
        email: String,
        password: String,
        displayName: String,
        company: String,
        role: UserRole = UserRole.CLIENT
    ): Flow<Resource<AgencyUser>>

    /**
     * Authenticate via Google Sign-In credential token.
     */
    fun signInWithGoogle(idToken: String): Flow<Resource<AgencyUser>>

    /**
     * Dispatch account password recovery email.
     */
    fun sendPasswordResetEmail(email: String): Flow<Resource<Unit>>

    /**
     * Terminate active authenticated session.
     */
    fun signOut(): Flow<Resource<Unit>>

    /**
     * Refresh current user profile and verification status.
     */
    fun reloadUser(): Flow<Resource<AgencyUser>>
}
