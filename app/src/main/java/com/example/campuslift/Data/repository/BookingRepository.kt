package com.example.campuslift.Data.repository

import com.example.campuslift.Data.dto.ApproveBookingRequest
import com.example.campuslift.Data.dto.BookingDto
import com.example.campuslift.Data.dto.BookingWithTripDto
import com.example.campuslift.Data.dto.CreateBookingRequest
import com.example.campuslift.Data.dto.SimpleMessageResponse
import com.example.campuslift.Data.remote.RetrofitClient
import retrofit2.HttpException

class BookingRepository {

    private val api = RetrofitClient.api

    private fun extractErrorMessage(e: Throwable): Exception {
        if (e is HttpException) {
            val body = e.response()?.errorBody()?.string()
            val message = body?.trim('"')?.takeIf { it.isNotBlank() } ?: e.message()
            return Exception(message)
        }
        return Exception(e.message ?: "Something went wrong")
    }

    suspend fun create(tripId: String, seats: Int): Result<BookingDto> =
        try {
            Result.success(api.createBooking(CreateBookingRequest(tripId, seats)))
        } catch (e: Exception) {
            Result.failure(extractErrorMessage(e))
        }

    suspend fun myBookings(): Result<List<BookingWithTripDto>> =
        try {
            Result.success(api.myBookings())
        } catch (e: Exception) {
            Result.failure(extractErrorMessage(e))
        }

    suspend fun forTrip(tripId: String): Result<List<BookingWithTripDto>> =
        try {
            Result.success(api.bookingsForTrip(tripId))
        } catch (e: Exception) {
            Result.failure(extractErrorMessage(e))
        }

    suspend fun approve(bookingId: String, approve: Boolean): Result<BookingDto> =
        try {
            Result.success(api.approveBooking(bookingId, ApproveBookingRequest(approve)))
        } catch (e: Exception) {
            Result.failure(extractErrorMessage(e))
        }

    suspend fun cancel(bookingId: String): Result<SimpleMessageResponse> =
        try {
            Result.success(api.cancelBooking(bookingId))
        } catch (e: Exception) {
            Result.failure(extractErrorMessage(e))
        }

    suspend fun confirmPickup(bookingId: String): Result<SimpleMessageResponse> =
        try {
            Result.success(api.confirmPickup(bookingId))
        } catch (e: Exception) {
            Result.failure(extractErrorMessage(e))
        }
}