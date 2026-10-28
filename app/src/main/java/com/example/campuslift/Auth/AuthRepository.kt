package com.example.campuslift.Auth

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Single source of truth for all Firebase Authentication operations.
 * Exposes auth state as a Flow so Compose UIs can react to sign-in/out events.
 */
class AuthRepository(
    private val auth: FirebaseAuth = Firebase.auth,
    /**
     * Called when the user signs out. Inject your CredentialManager cleanup here
     * (e.g. GoogleSignInHelper::clearCredentialState) so AuthRepository stays
     * unit-testable without an Android Context.
     */
    private val onSignOut: suspend () -> Unit = {}
) {
    private val TAG = "AuthRepository"

    val authState: Flow<AuthState> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            Log.d(TAG, "Auth state changed: uid=${user?.uid ?: "null"}")
            trySend(
                if (user != null) {
                    AuthState.Authenticated(
                        userId = user.uid,
                        email = user.email,
                        displayName = user.displayName
                    )
                } else {
                    AuthState.Unauthenticated
                }
            )
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            Log.d(TAG, "signInWithEmail: $email")
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
                ?: return Result.failure(Exception("Sign-in returned null user"))
            Log.i(TAG, "Sign-in success: ${user.uid}")
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Sign-in failed", e)
            Result.failure(e)
        }
    }

    suspend fun register(email: String, password: String): Result<FirebaseUser> {
        return try {
            Log.d(TAG, "register: $email")
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
                ?: return Result.failure(Exception("Registration returned null user"))
            Log.i(TAG, "Registration success: ${user.uid}")
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Registration failed", e)
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser> {
        return try {
            Log.d(TAG, "signInWithGoogle")
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user
                ?: return Result.failure(Exception("Google sign-in returned null user"))
            Log.i(TAG, "Google sign-in success: ${user.uid}")
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Google sign-in failed", e)
            Result.failure(e)
        }
    }

    /**
     * Returns a cached Firebase ID token (refreshed if expired).
     * Use for UI gating / quick checks.
     */
    suspend fun getIdToken(): String? {
        return try {
            val token = auth.currentUser?.getIdToken(false)?.await()?.token
            Log.d(TAG, "Cached ID token: ${token?.take(20)}...")
            token
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get cached ID token", e)
            null
        }
    }

    /**
     * Forces a fresh Firebase ID token from the server.
     * Use when calling APIs where token freshness matters
     * (e.g. after a role change, or when the cached token might be near expiry).
     */
    suspend fun getFreshIdToken(): String? {
        return try {
            val token = auth.currentUser?.getIdToken(true)?.await()?.token
            Log.d(TAG, "Fresh ID token: ${token?.take(20)}...")
            token
        } catch (e: Exception) {
            Log.e(TAG, "Failed to refresh ID token", e)
            null
        }
    }

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    /**
     * Signs out from Firebase, then clears Credential Manager state.
     *
     * Clearing Credential Manager state prevents the next sign-in() call
     * from trying to silently reuse the previous Google account, which is
     * the source of the "No credentials" error on some OEM devices.
     *
     * Note: this does NOT sign the user out of Google on the device —
     * it only clears the authorization grant cached by Credential Manager.
     */
    suspend fun signOut() {
        Log.d(TAG, "signOut: ${auth.currentUser?.uid}")
        try {
            auth.signOut()
        } catch (e: Exception) {
            Log.e(TAG, "Firebase signOut failed", e)
        }
        // Always clear Credential Manager state, even if Firebase signOut failed.
        try {
            onSignOut()
        } catch (e: Exception) {
            Log.e(TAG, "Credential Manager clearCredentialState failed", e)
        }
    }
}