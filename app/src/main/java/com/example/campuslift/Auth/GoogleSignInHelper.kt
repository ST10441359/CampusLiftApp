package com.example.campuslift.Auth

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
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
 * Handles Google Sign-In through Android Credential Manager.
 *
 * Returns the Google ID token, which is then passed to Firebase Auth.
 */
class GoogleSignInHelper(
    private val context: Context
) {

    private val TAG = "GoogleSignInHelper"

    private val credentialManager =
        CredentialManager.create(context)

    /**
     * Starts Google Sign-In.
     *
     * Returns the Google ID token on success.
     */
    suspend fun signIn(): Result<String> {

        return try {

            Log.d(TAG, "Starting Google Sign-In flow")

            val googleIdOption =
                GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(
                        context.getString(
                            R.string.default_web_client_id
                        )
                    )
                    .setAutoSelectEnabled(false)
                    .build()

            val request =
                GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

            val response: GetCredentialResponse =
                credentialManager.getCredential(
                    context = context,
                    request = request
                )

            val credential = response.credential

            if (
                credential is CustomCredential &&
                credential.type ==
                GoogleIdTokenCredential
                    .TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {

                val googleIdToken =
                    GoogleIdTokenCredential
                        .createFrom(credential.data)

                Log.i(TAG, "Google ID token received")

                Result.success(
                    googleIdToken.idToken
                )

            } else {

                Log.e(
                    TAG,
                    "Unexpected credential type: ${credential.type}"
                )

                Result.failure(
                    Exception("Unexpected credential type")
                )
            }

        } catch (e: GetCredentialException) {

            Log.e(
                TAG,
                "Credential Manager error: ${e.message}",
                e
            )

            Result.failure(e)

        } catch (e: GoogleIdTokenParsingException) {

            Log.e(
                TAG,
                "Failed to parse Google ID token",
                e
            )

            Result.failure(e)

        } catch (e: Exception) {

            Log.e(
                TAG,
                "Unexpected error during Google Sign-In",
                e
            )

            Result.failure(e)
        }
    }

    /**
     * Clears Credential Manager state when the user logs out.
     */
    suspend fun clearCredentialState(): Result<Unit> {

        return try {

            Log.d(
                TAG,
                "Clearing Credential Manager state"
            )

            credentialManager.clearCredentialState(
                ClearCredentialStateRequest()
            )

            Log.i(
                TAG,
                "Credential Manager state cleared"
            )

            Result.success(Unit)

        } catch (e: Exception) {

            Log.e(
                TAG,
                "Failed to clear Credential Manager state",
                e
            )

            Result.failure(e)
        }
    }
}