package com.example.campuslift.Auth

/**
 * Represents the possible authentication states in CampusLift.
 * Used as the source of truth for navigation decisions (login vs home).
 */
sealed class AuthState {
    /** Initial state while Firebase resolves the current user. */
    object Loading : AuthState()

    /** User is not signed in. */
    object Unauthenticated : AuthState()

    /** User is signed in and has a valid Firebase session. */
    data class Authenticated(
        val userId: String,
        val email: String?,
        val displayName: String?
    ) : AuthState()

    /** Auth operation failed — used to surface errors to the UI. */
    data class Error(val message: String) : AuthState()
}