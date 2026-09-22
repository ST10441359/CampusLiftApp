package com.example.campuslift.ViewModels

import com.example.campuslift.Data.dto.CreateVehicleRequest
import com.example.campuslift.Data.dto.UpdateVehicleRequest
import com.example.campuslift.Data.dto.VehicleDto
import com.example.campuslift.Data.repository.VehicleRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VehicleViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repo: VehicleRepository
    private lateinit var viewModel: VehicleViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repo = mockk()
        viewModel = VehicleViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun sampleVehicle(id: String = "v1") = VehicleDto(
        id = id,
        userId = "u1",
        make = "Toyota",
        model = "Etios",
        year = 2020,
        color = "Silver",
        licensePlate = "HB 42 KZN",
        seats = 4,
        isActive = true,
        createdAt = null
    )

    @Test
    fun `loadVehicles success populates list`() = runTest {
        coEvery { repo.myVehicles() } returns Result.success(
            listOf(sampleVehicle("v1"), sampleVehicle("v2"))
        )

        viewModel.loadVehicles()
        advanceUntilIdle()

        assertEquals(2, viewModel.vehicles.value.size)
        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun `loadVehicles failure sets error`() = runTest {
        coEvery { repo.myVehicles() } returns Result.failure(Exception("Auth required"))

        viewModel.loadVehicles()
        advanceUntilIdle()

        assertEquals("Auth required", viewModel.error.value)
        assertTrue(viewModel.vehicles.value.isEmpty())
    }

    @Test
    fun `create success reloads vehicles and invokes onDone true`() = runTest {
        val body = CreateVehicleRequest(
            make = "Toyota",
            model = "Etios",
            year = 2020,
            color = "Silver",
            licensePlate = "HB 42 KZN",
            seats = 4
        )
        coEvery { repo.create(body) } returns Result.success(sampleVehicle())
        coEvery { repo.myVehicles() } returns Result.success(listOf(sampleVehicle()))

        var callbackResult: Boolean? = null
        viewModel.create(body) { callbackResult = it }
        advanceUntilIdle()

        assertEquals(true, callbackResult)
        assertEquals(1, viewModel.vehicles.value.size)
    }

    @Test
    fun `create failure sets error and invokes onDone false`() = runTest {
        val body = CreateVehicleRequest("", "", null, null, "", 0)
        coEvery { repo.create(body) } returns Result.failure(Exception("Invalid plate"))
        var callbackResult: Boolean? = null

        viewModel.create(body) { callbackResult = it }
        advanceUntilIdle()

        assertEquals(false, callbackResult)
        assertEquals("Invalid plate", viewModel.error.value)
    }

    @Test
    fun `update success reloads and invokes onDone true`() = runTest {
        val body = UpdateVehicleRequest(color = "Red")
        coEvery { repo.update("v1", body) } returns Result.success(sampleVehicle("v1"))
        coEvery { repo.myVehicles() } returns Result.success(listOf(sampleVehicle("v1")))

        var callbackResult: Boolean? = null
        viewModel.update("v1", body) { callbackResult = it }
        advanceUntilIdle()

        assertEquals(true, callbackResult)
    }

    @Test
    fun `update failure sets error and invokes onDone false`() = runTest {
        val body = UpdateVehicleRequest(color = "Red")
        coEvery { repo.update("v1", body) } returns Result.failure(Exception("Not found"))
        var callbackResult: Boolean? = null

        viewModel.update("v1", body) { callbackResult = it }
        advanceUntilIdle()

        assertEquals(false, callbackResult)
        assertEquals("Not found", viewModel.error.value)
    }

    @Test
    fun `delete success reloads and invokes onDone true`() = runTest {
        coEvery { repo.delete("v1") } returns Result.success(Unit)
        coEvery { repo.myVehicles() } returns Result.success(emptyList())

        var callbackResult: Boolean? = null
        viewModel.delete("v1") { callbackResult = it }
        advanceUntilIdle()

        assertEquals(true, callbackResult)
        assertTrue(viewModel.vehicles.value.isEmpty())
    }

    @Test
    fun `delete failure sets error and invokes onDone false`() = runTest {
        coEvery { repo.delete("v1") } returns Result.failure(Exception("Permission denied"))
        var callbackResult: Boolean? = null

        viewModel.delete("v1") { callbackResult = it }
        advanceUntilIdle()

        assertEquals(false, callbackResult)
        assertEquals("Permission denied", viewModel.error.value)
    }

    @Test
    fun `clearError resets error`() = runTest {
        coEvery { repo.myVehicles() } returns Result.failure(Exception("Boom"))
        viewModel.loadVehicles()
        advanceUntilIdle()
        assertEquals("Boom", viewModel.error.value)

        viewModel.clearError()
        assertNull(viewModel.error.value)
    }
}