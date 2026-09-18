package com.example.campuslift.Data.dto

import com.google.gson.annotations.SerializedName

data class TripWithAvailabilityDto(
    @SerializedName("id") val id: String,
    @SerializedName("driverId") val driverId: String,
    @SerializedName("vehicleId") val vehicleId: String?,
    @SerializedName("fromLocation") val fromLocation: String,
    @SerializedName("toLocation") val toLocation: String,
    @SerializedName("fromLat") val fromLat: Double?,
    @SerializedName("fromLng") val fromLng: Double?,
    @SerializedName("toLat") val toLat: Double?,
    @SerializedName("toLng") val toLng: Double?,
    @SerializedName("eventTime") val eventTime: String?,
    @SerializedName("pricePerSeat") val pricePerSeat: Double,
    @SerializedName("totalSeats") val totalSeats: Int,
    @SerializedName("description") val description: String?,
    @SerializedName("isActive") val isActive: Boolean,
    @SerializedName("isComplete") val isComplete: Boolean,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("seatsTaken") val seatsTaken: Int,
    @SerializedName("seatsRemaining") val seatsRemaining: Int
)

/**
 * Envelope returned by GET /api/trips and GET /api/trips/mine.
 */
data class TripListResponse(
    @SerializedName("items") val items: List<TripWithAvailabilityDto>,
    @SerializedName("limit") val limit: Int,
    @SerializedName("offset") val offset: Int,
    @SerializedName("count") val count: Int
)

data class CreateTripRequest(
    @SerializedName("vehicleId") val vehicleId: String?,
    @SerializedName("fromLocation") val fromLocation: String,
    @SerializedName("toLocation") val toLocation: String,
    @SerializedName("fromLat") val fromLat: Double?,
    @SerializedName("fromLng") val fromLng: Double?,
    @SerializedName("toLat") val toLat: Double?,
    @SerializedName("toLng") val toLng: Double?,
    @SerializedName("eventTime") val eventTime: String,  // ISO 8601 with Z or offset
    @SerializedName("pricePerSeat") val pricePerSeat: Double,
    @SerializedName("totalSeats") val totalSeats: Int,
    @SerializedName("description") val description: String?
)

data class UpdateTripRequest(
    @SerializedName("vehicleId") val vehicleId: String? = null,
    @SerializedName("fromLocation") val fromLocation: String? = null,
    @SerializedName("toLocation") val toLocation: String? = null,
    @SerializedName("fromLat") val fromLat: Double? = null,
    @SerializedName("fromLng") val fromLng: Double? = null,
    @SerializedName("toLat") val toLat: Double? = null,
    @SerializedName("toLng") val toLng: Double? = null,
    @SerializedName("eventTime") val eventTime: String? = null,
    @SerializedName("pricePerSeat") val pricePerSeat: Double? = null,
    @SerializedName("totalSeats") val totalSeats: Int? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("isActive") val isActive: Boolean? = null
)

data class CancelTripResponse(
    @SerializedName("message") val message: String?,
    @SerializedName("tripId") val tripId: String?,
    @SerializedName("bookingsCancelled") val bookingsCancelled: Int?
)

data class CompleteTripResponse(
    @SerializedName("message") val message: String?,
    @SerializedName("tripId") val tripId: String?
)