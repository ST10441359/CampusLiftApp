package com.example.campuslift.Data.remote

import com.example.campuslift.Data.dto.*
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // ---------------- USERS ----------------
    @POST("api/users/sync")
    suspend fun syncUser(@Body body: SyncUserRequest): UserDto

    @GET("api/users/me")
    suspend fun me(): UserDto

    // ---------------- VEHICLES ----------------
    @GET("api/vehicles")
    suspend fun myVehicles(): List<VehicleDto>

    @GET("api/vehicles/{id}")
    suspend fun getVehicle(@Path("id") id: String): VehicleDto

    @POST("api/vehicles")
    suspend fun createVehicle(@Body body: CreateVehicleRequest): VehicleDto

    @PATCH("api/vehicles/{id}")
    suspend fun updateVehicle(
        @Path("id") id: String,
        @Body body: UpdateVehicleRequest
    ): VehicleDto

    @DELETE("api/vehicles/{id}")
    suspend fun deleteVehicle(@Path("id") id: String)

    // ---------------- TRIPS ----------------
    @GET("api/trips")
    suspend fun searchTrips(
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
        @Query("date") date: String? = null,       // ISO date, e.g. "2026-12-01"
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): TripListResponse

    @GET("api/trips/mine")
    suspend fun myTrips(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): TripListResponse

    @GET("api/trips/{id}")
    suspend fun getTrip(@Path("id") id: String): TripWithAvailabilityDto

    @POST("api/trips")
    suspend fun createTrip(@Body body: CreateTripRequest): TripWithAvailabilityDto

    @PATCH("api/trips/{id}")
    suspend fun updateTrip(
        @Path("id") id: String,
        @Body body: UpdateTripRequest
    ): TripWithAvailabilityDto

    @POST("api/trips/{id}/cancel")
    suspend fun cancelTrip(@Path("id") id: String): CancelTripResponse

    @POST("api/trips/{id}/complete")
    suspend fun completeTrip(@Path("id") id: String): CompleteTripResponse

    // ---------------- BOOKINGS ----------------
    @POST("api/bookings")
    suspend fun createBooking(@Body body: CreateBookingRequest): BookingDto

    @GET("api/bookings/mine")
    suspend fun myBookings(): List<BookingWithTripDto>

    @GET("api/bookings/trip/{tripId}")
    suspend fun bookingsForTrip(@Path("tripId") tripId: String): List<BookingWithTripDto>

    @POST("api/bookings/{id}/approve")
    suspend fun approveBooking(
        @Path("id") bookingId: String,
        @Body body: ApproveBookingRequest
    ): BookingDto

    @POST("api/bookings/{id}/cancel")
    suspend fun cancelBooking(@Path("id") bookingId: String): SimpleMessageResponse

    @POST("api/bookings/{id}/pickup-confirm")
    suspend fun confirmPickup(@Path("id") bookingId: String): SimpleMessageResponse

    // ---------------- NOTIFICATIONS ----------------
    @GET("api/notifications")
    suspend fun notifications(
        @Query("unreadOnly") unreadOnly: Boolean? = null,
        @Query("limit") limit: Int = 30,
        @Query("offset") offset: Int = 0
    ): NotificationListResponse

    @GET("api/notifications/unread-count")
    suspend fun unreadCount(): UnreadCountResponse

    @PATCH("api/notifications/{id}")
    suspend fun markNotificationRead(
        @Path("id") id: String,
        @Body body: UpdateNotificationRequest
    ): NotificationDto

    @POST("api/notifications/mark-all-read")
    suspend fun markAllNotificationsRead(): MarkAllReadResponse

    @DELETE("api/notifications/{id}")
    suspend fun deleteNotification(@Path("id") id: String)
}