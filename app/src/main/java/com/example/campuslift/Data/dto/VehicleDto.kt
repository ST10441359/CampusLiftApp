package com.example.campuslift.Data.dto

import com.google.gson.annotations.SerializedName

data class VehicleDto(
    @SerializedName("id") val id: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("make") val make: String,
    @SerializedName("model") val model: String,
    @SerializedName("year") val year: Int?,
    @SerializedName("color") val color: String?,
    @SerializedName("licensePlate") val licensePlate: String?,
    @SerializedName("seats") val seats: Int,
    @SerializedName("isActive") val isActive: Boolean,
    @SerializedName("createdAt") val createdAt: String?
)

data class CreateVehicleRequest(
    @SerializedName("make") val make: String,
    @SerializedName("model") val model: String,
    @SerializedName("year") val year: Int?,
    @SerializedName("color") val color: String?,
    @SerializedName("licensePlate") val licensePlate: String,
    @SerializedName("seats") val seats: Int
)

data class UpdateVehicleRequest(
    @SerializedName("make") val make: String? = null,
    @SerializedName("model") val model: String? = null,
    @SerializedName("year") val year: Int? = null,
    @SerializedName("color") val color: String? = null,
    @SerializedName("licensePlate") val licensePlate: String? = null,
    @SerializedName("seats") val seats: Int? = null,
    @SerializedName("isActive") val isActive: Boolean? = null
)