package com.example.campuslift.Auth

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.campuslift.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import java.security.SecureRandom
import java.util.Base64

/**
 * Handles Google Sign-In through Android Credential Manager.
 *
 * Uses a two-step strategy:
 *   1. Silent attempt with previously-authorized accounts (fast path)
 *   2. Fallback to full account picker if no authorized account exists
 *
 * This solves the "No credentials" error that happens after sign-out,
 * when no account is authorized yet but the user still wants to pick one.
 */
class GoogleSignInHelper(
    private val context: Context
) {

    private val TAG = "GoogleSignInHelper"

    private val credentialManager =
        CredentialManager.create(context)

    /**
     * Result types so the UI can distinguish cancellation from real errors.
     */
    sealed class SignInResult {
        data class Success(val idToken: String) : SignInResult()
        data object Cancelled : SignInResult()
        data class Failure(val exception: Throwable) : SignInResult()
    }

    /**
     * Starts Google Sign-In.
     *
     * Returns a [SignInResult] so the UI can react appropriately:
     * - Success → pass idToken to Firebase
     * - Cancelled → show friendly snackbar, no error
     * - Failure → show error, log it
     */
    suspend fun signIn(): SignInResult {
        Log.d(TAG, "Starting Google Sign-In (step 1: authorized accounts)")

        // Step 1: try previously authorized accounts (silent, fast)
        val firstAttempt = tryRequestCredentials(filterByAuthorized = true)

        when (firstAttempt) {
            is SignInResult.Success -> return firstAttempt

            is SignInResult.Cancelled -> {
                // User explicitly dismissed the picker → don't retry
                Log.d(TAG, "User cancelled at authorized-accounts step")
                return firstAttempt
            }

            is SignInResult.Failure -> {
                // Only fall back when we know there are no authorized accounts.
                // Other failures (network, misconfig) are terminal.
                if (firstAttempt.exception !is NoCredentialException) {
                    Log.e(TAG, "Terminal failure in step 1", firstAttempt.exception)
                    return firstAttempt
                }
                Log.d(TAG, "No authorized accounts found, falling back to full picker")
            }
        }

        // Step 2: full account picker (all Google accounts on device)
        Log.d(TAG, "Starting Google Sign-In (step 2: full picker)")
        return tryRequestCredentials(filterByAuthorized = false)
    }

    private suspend fun tryRequestCredentials(
        filterByAuthorized: Boolean
    ): SignInResult {
        return try {
            val googleIdOption =
                GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(filterByAuthorized)
                    .setServerClientId(
                        context.getString(R.string.default_web_client_id)
                    )
                    .setNonce(generateSecureRandomNonce())
                    // Auto-select only makes sense for the silent path.
                    // Turning it off for the full picker avoids surprising
                    // auto-logins on multi-account devices.
                    .setAutoSelectEnabled(filterByAuthorized)
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
                GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdToken =
                    GoogleIdTokenCredential.createFrom(credential.data)

                Log.i(TAG, "Google ID token received")
                SignInResult.Success(googleIdToken.idToken)
            } else {
                Log.e(TAG, "Unexpected credential type: ${credential.type}")
                SignInResult.Failure(Exception("Unexpected credential type"))
            }

        } catch (e: GetCredentialCancellationException) {
            // User tapped back / X on the picker. Not an error.
            Log.d(TAG, "User cancelled Google Sign-In")
            SignInResult.Cancelled

        } catch (e: NoCredentialException) {
            // No accounts match the filter — caller decides whether to retry.
            Log.d(TAG, "NoCredentialException (filter=$filterByAuthorized)")
            SignInResult.Failure(e)

        } catch (e: GetCredentialException) {
            Log.e(TAG, "Credential Manager error: ${e.message}", e)
            SignInResult.Failure(e)

        } catch (e: GoogleIdTokenParsingException) {
            Log.e(TAG, "Failed to parse Google ID token", e)
            SignInResult.Failure(e)

        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during Google Sign-In", e)
            SignInResult.Failure(e)
        }
    }

    /**
     * Clears Credential Manager state when the user logs out.
     *
     * This removes the cached authorization grant so the next sign-in
     * shows the account picker instead of silently reusing the previous
     * account. It does NOT sign the user out of Google on the device.
     */
    suspend fun clearCredentialState(): Result<Unit> {
        return try {
            Log.d(TAG, "Clearing Credential Manager state")
            credentialManager.clearCredentialState(
                ClearCredentialStateRequest()
            )
            Log.i(TAG, "Credential Manager state cleared")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear Credential Manager state", e)
            Result.failure(e)
        }
    }

    private fun generateSecureRandomNonce(): String {
        val bytes = ByteArray(32)
        SecureRandom().nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }
}