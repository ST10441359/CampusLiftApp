package com.example.campuslift.Data.repository

import com.example.campuslift.Data.dto.CreateVehicleRequest
import com.example.campuslift.Data.dto.UpdateVehicleRequest
import com.example.campuslift.Data.dto.VehicleDto
import com.example.campuslift.Data.remote.RetrofitClient

class VehicleRepository {
    private val api = RetrofitClient.api

    suspend fun myVehicles(): Result<List<VehicleDto>> =
        runCatching { api.myVehicles() }

    suspend fun get(id: String): Result<VehicleDto> =
        runCatching { api.getVehicle(id) }

    suspend fun create(body: CreateVehicleRequest): Result<VehicleDto> =
        runCatching { api.createVehicle(body) }

    suspend fun update(id: String, body: UpdateVehicleRequest): Result<VehicleDto> =
        runCatching { api.updateVehicle(id, body) }

    suspend fun delete(id: String): Result<Unit> =
        runCatching { api.deleteVehicle(id) }
}