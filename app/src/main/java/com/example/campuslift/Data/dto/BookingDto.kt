package com.example.campuslift.Data.dto

import com.google.gson.annotations.SerializedName

data class BookingDto(
    @SerializedName("id") val id: String,
    @SerializedName("tripId") val tripId: String,
    @SerializedName("passengerId") val passengerId: String,
    @SerializedName("seatsRequested") val seatsRequested: Int,
    @SerializedName("approval") val approval: String, // "pending" | "approved" | "rejected"
    @SerializedName("approvedTime") val approvedTime: String?,
    @SerializedName("bookingTime") val bookingTime: String?,
    @SerializedName("cancellationTime") val cancellationTime: String?,
    @SerializedName("pickupConfirmed") val pickupConfirmed: Boolean
)

data class BookingWithTripDto(
    @SerializedName("id") val id: String,
    @SerializedName("tripId") val tripId: String,
    @SerializedName("passengerId") val passengerId: String,
    @SerializedName("seatsRequested") val seatsRequested: Int,
    @SerializedName("approval") val approval: String,
    @SerializedName("approvedTime") val approvedTime: String?,
    @SerializedName("bookingTime") val bookingTime: String?,
    @SerializedName("cancellationTime") val cancellationTime: String?,
    @SerializedName("pickupConfirmed") val pickupConfirmed: Boolean,
    @SerializedName("fromLocation") val fromLocation: String,
    @SerializedName("toLocation") val toLocation: String,
    @SerializedName("eventTime") val eventTime: String?,
    @SerializedName("pricePerSeat") val pricePerSeat: Double
)

data class CreateBookingRequest(
    @SerializedName("tripId") val tripId: String,
    @SerializedName("seatsRequested") val seatsRequested: Int
)

data class ApproveBookingRequest(
    @SerializedName("approve") val approve: Boolean
)

data class SimpleMessageResponse(
    @SerializedName("message") val message: String?,
    @SerializedName("bookingId") val bookingId: String?
)