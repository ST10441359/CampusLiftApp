package com.example.campuslift.Data.repository

import com.example.campuslift.Data.dto.CancelTripResponse
import com.example.campuslift.Data.dto.CompleteTripResponse
import com.example.campuslift.Data.dto.CreateTripRequest
import com.example.campuslift.Data.dto.TripListResponse
import com.example.campuslift.Data.dto.TripWithAvailabilityDto
import com.example.campuslift.Data.dto.UpdateTripRequest
import com.example.campuslift.Data.remote.RetrofitClient

class TripRepository {
    private val api = RetrofitClient.api

    suspend fun search(
        from: String? = null,
        to: String? = null,
        date: String? = null,
        limit: Int = 20,
        offset: Int = 0
    ): Result<TripListResponse> =
        runCatching { api.searchTrips(from, to, date, limit, offset) }

    suspend fun mine(limit: Int = 20, offset: Int = 0): Result<TripListResponse> =
        runCatching { api.myTrips(limit, offset) }

    suspend fun get(id: String): Result<TripWithAvailabilityDto> =
        runCatching { api.getTrip(id) }

    suspend fun create(body: CreateTripRequest): Result<TripWithAvailabilityDto> =
        runCatching { api.createTrip(body) }

    suspend fun update(id: String, body: UpdateTripRequest): Result<TripWithAvailabilityDto> =
        runCatching { api.updateTrip(id, body) }

    suspend fun cancel(id: String): Result<CancelTripResponse> =
        runCatching { api.cancelTrip(id) }

    suspend fun complete(id: String): Result<CompleteTripResponse> =
        runCatching { api.completeTrip(id) }
}