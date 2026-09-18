package com.example.campuslift.Data.remote

import com.google.firebase.auth.FirebaseAuth
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Injects the current Firebase UID into the X-Firebase-Uid header
 * on every request. Our API resolves that to the internal Supabase user.
 */
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val request = chain.request().newBuilder().apply {
            if (!uid.isNullOrBlank()) {
                addHeader("X-Firebase-Uid", uid)
            }
            addHeader("Accept", "application/json")
        }.build()
        return chain.proceed(request)
    }
}