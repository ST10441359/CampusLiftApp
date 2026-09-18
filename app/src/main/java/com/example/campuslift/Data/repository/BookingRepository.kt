package com.example.campuslift.Data.repository

import com.example.campuslift.Data.dto.ApproveBookingRequest
import com.example.campuslift.Data.dto.BookingDto
import com.example.campuslift.Data.dto.BookingWithTripDto
import com.example.campuslift.Data.dto.CreateBookingRequest
import com.example.campuslift.Data.dto.SimpleMessageResponse
import com.example.campuslift.Data.remote.RetrofitClient

class BookingRepository {

    private val api = RetrofitClient.api

    suspend fun create(tripId: String, seats: Int): Result<BookingDto> =
        runCatching { api.createBooking(CreateBookingRequest(tripId, seats)) }

    suspend fun myBookings(): Result<List<BookingWithTripDto>> =
        runCatching { api.myBookings() }

    suspend fun forTrip(tripId: String): Result<List<BookingWithTripDto>> =
        runCatching { api.bookingsForTrip(tripId) }

    suspend fun approve(bookingId: String, approve: Boolean): Result<BookingDto> =
        runCatching { api.approveBooking(bookingId, ApproveBookingRequest(approve)) }

    suspend fun cancel(bookingId: String): Result<SimpleMessageResponse> =
        runCatching { api.cancelBooking(bookingId) }

    suspend fun confirmPickup(bookingId: String): Result<SimpleMessageResponse> =
        runCatching { api.confirmPickup(bookingId) }
}