package com.example.campuslift.Data.dto

import com.google.gson.annotations.SerializedName

data class PublicUserSummaryDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String?,
    @SerializedName("surname") val surname: String?,
    @SerializedName("profilePicture") val profilePicture: String?
)

data class PublicVehicleSummaryDto(
    @SerializedName("id") val id: String,
    @SerializedName("make") val make: String?,
    @SerializedName("model") val model: String?,
    @SerializedName("year") val year: Int?,
    @SerializedName("color") val color: String?,
    @SerializedName("licensePlate") val licensePlate: String?,
    @SerializedName("seats") val seats: Int
)