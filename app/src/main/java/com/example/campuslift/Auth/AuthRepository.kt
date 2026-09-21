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
 *
 * Reference: https://firebase.google.com/docs/auth/android/start
 */
class AuthRepository(
    private val auth: FirebaseAuth = Firebase.auth
) {
    private val TAG = "AuthRepository"

    /**
     * Reactive auth state. Emits on every login/logout and on app start.
     * Uses callbackFlow to bridge Firebase's listener to Kotlin Flow.
     */
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

    /** Email + password sign-in. */
    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            Log.d(TAG, "signInWithEmail: $email")
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                Log.i(TAG, "Sign-in success: ${user.uid}")
                Result.success(user)
            } else {
                Result.failure(Exception("Sign-in returned null user"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Sign-in failed", e)
            Result.failure(e)
        }
    }

    /** Register a new account with email + password. */
    suspend fun register(email: String, password: String): Result<FirebaseUser> {
        return try {
            Log.d(TAG, "register: $email")
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                Log.i(TAG, "Registration success: ${user.uid}")
                Result.success(user)
            } else {
                Result.failure(Exception("Registration returned null user"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Registration failed", e)
            Result.failure(e)
        }
    }

    /** Sign in with Google using the ID token from Credential Manager. */
    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser> {
        return try {
            Log.d(TAG, "signInWithGoogle")
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user
            if (user != null) {
                Log.i(TAG, "Google sign-in success: ${user.uid}")
                Result.success(user)
            } else {
                Result.failure(Exception("Google sign-in returned null user"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Google sign-in failed", e)
            Result.failure(e)
        }
    }

    /**
     * Returns the current user's Firebase ID token.
     * This is what you send to Joshua's API as "Authorization: Bearer <token>".
     */
    suspend fun getIdToken(): String? {
        return try {
            val token = auth.currentUser?.getIdToken(false)?.await()?.token
            Log.d(TAG, "ID token retrieved: ${token?.take(20)}...")
            token
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get ID token", e)
            null
        }
    }

    /** Returns current user's UID — becomes the primary key in Supabase. */
    fun getCurrentUserId(): String? = auth.currentUser?.uid

    /** Signs out the current user. */
    fun signOut() {
        Log.d(TAG, "signOut: ${auth.currentUser?.uid}")
        auth.signOut()
    }
}