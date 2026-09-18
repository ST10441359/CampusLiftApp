package com.example.campuslift.Data.repository

import com.example.campuslift.Data.dto.SyncUserRequest
import com.example.campuslift.Data.dto.UserDto
import com.example.campuslift.Data.remote.RetrofitClient

class UserRepository {

    private val api = RetrofitClient.api

    /**
     * Creates the Supabase user row if missing, or returns the existing one.
     */
    suspend fun sync(
        firebaseUid: String,
        email: String?,
        name: String?,
        surname: String?
    ): Result<UserDto> = runCatching {
        api.syncUser(SyncUserRequest(firebaseUid, email, name, surname))
    }

    /**
     * Fetches the current user based on the X-Firebase-Uid header.
     */
    suspend fun me(): Result<UserDto> = runCatching {
        api.me()
    }
}