package com.example.campuslift.Auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.example.campuslift.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException

/**
 * Helper class that wraps Credential Manager for Google Sign-In.
 * Returns the Google ID token, which is then passed to Firebase Auth.
 *
 * Reference: https://developer.android.com/identity/sign-in/credential-manager-siwg
 */
class GoogleSignInHelper(private val context: Context) {

    private val TAG = "GoogleSignInHelper"
    private val credentialManager = CredentialManager.create(context)

    /**
     * Triggers Google Sign-In. Returns a Result containing the Google ID token.
     * The token is then passed to AuthRepository.signInWithGoogle().
     */
    suspend fun signIn(): Result<String> {
        return try {
            Log.d(TAG, "Starting Google Sign-In flow")

            // Configure Google ID option
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false) // Show all Google accounts on device
                .setServerClientId(context.getString(R.string.default_web_client_id))
                .setAutoSelectEnabled(false)
                .build()

            // Build Credential Manager request
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            // Execute the request
            val response: GetCredentialResponse = credentialManager.getCredential(
                context = context,
                request = request
            )

            // Extract ID token from response
            val credential = response.credential
            when {
                credential is CustomCredential &&
                        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL -> {
                    val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data)
                    Log.i(TAG, "Google ID token received")
                    Result.success(googleIdToken.idToken)
                }
                else -> {
                    Log.e(TAG, "Unexpected credential type: ${credential.type}")
                    Result.failure(Exception("Unexpected credential type"))
                }
            }
        } catch (e: GetCredentialException) {
            Log.e(TAG, "Credential Manager error: ${e.message}", e)
            Result.failure(e)
        } catch (e: GoogleIdTokenParsingException) {
            Log.e(TAG, "Failed to parse Google ID token", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during Google Sign-In", e)
            Result.failure(e)
        }
    }
}