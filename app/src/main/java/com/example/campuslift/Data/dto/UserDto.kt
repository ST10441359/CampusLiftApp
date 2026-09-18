package com.example.campuslift.Data.dto

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("id") val id: String,
    @SerializedName("firebaseUid") val firebaseUid: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("surname") val surname: String?,
    @SerializedName("studentNumber") val studentNumber: String?,
    @SerializedName("university") val university: String?,
    @SerializedName("isVerified") val isVerified: Boolean?,
    @SerializedName("profilePicture") val profilePicture: String?,
    @SerializedName("emergencyContact") val emergencyContact: String?,
    @SerializedName("language") val language: String?,
    @SerializedName("darkMode") val darkMode: Boolean?,
    @SerializedName("biometricEnabled") val biometricEnabled: Boolean?,
    @SerializedName("notificationEnabled") val notificationEnabled: Boolean?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?
)

data class SyncUserRequest(
    @SerializedName("firebaseUid") val firebaseUid: String,
    @SerializedName("email") val email: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("surname") val surname: String?
)