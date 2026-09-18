package com.gotechmedia.app.core.auth

import com.gotechmedia.app.domain.model.AgencyUser

/**
 * Role-Based Access Control enum for GoTech Media.
 */
enum class UserRole {
    PUBLIC,
    CLIENT,
    ADMIN;

    companion object {
        fun fromString(role: String?): UserRole {
            return when (role?.uppercase()) {
                "ADMIN" -> ADMIN
                "CLIENT" -> CLIENT
                else -> PUBLIC
            }
        }
    }
}

/**
 * Authentication state machine for reactive UI observation.
 */
sealed interface AuthState {
    data object Loading : AuthState
    data object Unauthenticated : AuthState
    data class Authenticated(val user: AgencyUser) : AuthState
    data class Error(val message: String) : AuthState
}
