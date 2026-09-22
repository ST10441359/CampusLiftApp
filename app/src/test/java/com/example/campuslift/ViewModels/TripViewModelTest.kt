package com.example.campuslift.ViewModels

import com.example.campuslift.Data.dto.CancelTripResponse
import com.example.campuslift.Data.dto.CompleteTripResponse
import com.example.campuslift.Data.dto.CreateTripRequest
import com.example.campuslift.Data.dto.TripListResponse
import com.example.campuslift.Data.dto.TripWithAvailabilityDto
import com.example.campuslift.Data.dto.UpdateTripRequest
import com.example.campuslift.Data.repository.TripRepository
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TripViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repo: TripRepository
    private lateinit var viewModel: TripViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repo = mockk()
        viewModel = TripViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun sampleTrip(id: String = "t1") = TripWithAvailabilityDto(
        id = id,
        driverId = "d1",
        vehicleId = "v1",
        fromLocation = "Home",
        toLocation = "Campus",
        fromLat = -29.85,
        fromLng = 31.02,
        toLat = -29.86,
        toLng = 31.03,
        eventTime = "2026-01-01T08:00:00Z",
        pricePerSeat = 20.0,
        totalSeats = 4,
        description = "Morning lift",
        isActive = true,
        isComplete = false,
        createdAt = null,
        seatsTaken = 1,
        seatsRemaining = 3,
        driver = null,
        vehicle = null
    )

    private fun sampleList(vararg items: TripWithAvailabilityDto) = TripListResponse(
        items = items.toList(),
        limit = 20,
        offset = 0,
        count = items.size
    )

    @Test
    fun `search success updates trips list`() = runTest {
        coEvery { repo.search(any(), any(), any(), 20, 0) } returns Result.success(
            sampleList(sampleTrip("t1"), sampleTrip("t2"))
        )

        viewModel.search()
        advanceUntilIdle()

        assertEquals(2, viewModel.trips.value.size)
        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun `search failure sets error`() = runTest {
        coEvery { repo.search(any(), any(), any(), 20, 0) } returns Result.failure(Exception("Bad request"))

        viewModel.search()
        advanceUntilIdle()

        assertEquals("Bad request", viewModel.error.value)
        assertTrue(viewModel.trips.value.isEmpty())
    }

    @Test
    fun `loadMine success populates trips`() = runTest {
        coEvery { repo.mine(20, 0) } returns Result.success(sampleList(sampleTrip("t3")))

        viewModel.loadMine()
        advanceUntilIdle()

        assertEquals(1, viewModel.trips.value.size)
        assertEquals("t3", viewModel.trips.value.first().id)
    }

    @Test
    fun `loadTrip success sets selected`() = runTest {
        coEvery { repo.get("t1") } returns Result.success(sampleTrip("t1"))

        viewModel.loadTrip("t1")
        advanceUntilIdle()

        assertNotNull(viewModel.selected.value)
        assertEquals("t1", viewModel.selected.value?.id)
    }

    @Test
    fun `loadTrip failure sets error`() = runTest {
        coEvery { repo.get("t1") } returns Result.failure(Exception("Not found"))

        viewModel.loadTrip("t1")
        advanceUntilIdle()

        assertEquals("Not found", viewModel.error.value)
        assertNull(viewModel.selected.value)
    }

    @Test
    fun `create success invokes onDone true`() = runTest {
        val body = CreateTripRequest(
            vehicleId = "v1",
            fromLocation = "A",
            toLocation = "B",
            fromLat = null, fromLng = null, toLat = null, toLng = null,
            eventTime = "2026-01-01T08:00:00Z",
            pricePerSeat = 20.0,
            totalSeats = 4,
            description = null
        )
        coEvery { repo.create(body) } returns Result.success(sampleTrip())
        var callbackResult: Boolean? = null

        viewModel.create(body) { callbackResult = it }
        advanceUntilIdle()

        assertEquals(true, callbackResult)
        assertNull(viewModel.error.value)
    }

    @Test
    fun `create failure sets error and invokes onDone false`() = runTest {
        val body = CreateTripRequest(
            vehicleId = null,
            fromLocation = "A",
            toLocation = "B",
            fromLat = null, fromLng = null, toLat = null, toLng = null,
            eventTime = "bad",
            pricePerSeat = -1.0,
            totalSeats = 0,
            description = null
        )
        coEvery { repo.create(body) } returns Result.failure(Exception("Validation failed"))
        var callbackResult: Boolean? = null

        viewModel.create(body) { callbackResult = it }
        advanceUntilIdle()

        assertEquals(false, callbackResult)
        assertEquals("Validation failed", viewModel.error.value)
    }

    @Test
    fun `update success sets selected and invokes onDone true`() = runTest {
        val body = UpdateTripRequest(pricePerSeat = 25.0)
        coEvery { repo.update("t1", body) } returns Result.success(sampleTrip("t1"))
        var callbackResult: Boolean? = null

        viewModel.update("t1", body) { callbackResult = it }
        advanceUntilIdle()

        assertEquals(true, callbackResult)
        assertNotNull(viewModel.selected.value)
    }

    @Test
    fun `cancel success triggers reload and invokes onDone true`() = runTest {
        coEvery { repo.cancel("t1") } returns Result.success(
            CancelTripResponse("Cancelled", "t1", 2)
        )
        coEvery { repo.mine(20, 0) } returns Result.success(sampleList())

        var callbackResult: Boolean? = null
        viewModel.cancel("t1") { callbackResult = it }
        advanceUntilIdle()

        assertEquals(true, callbackResult)
    }

    @Test
    fun `cancel failure sets error and invokes onDone false`() = runTest {
        coEvery { repo.cancel("t1") } returns Result.failure(Exception("Already cancelled"))
        var callbackResult: Boolean? = null

        viewModel.cancel("t1") { callbackResult = it }
        advanceUntilIdle()

        assertEquals(false, callbackResult)
        assertEquals("Already cancelled", viewModel.error.value)
    }

    @Test
    fun `complete success triggers reload and invokes onDone true`() = runTest {
        coEvery { repo.complete("t1") } returns Result.success(
            CompleteTripResponse("Completed", "t1")
        )
        coEvery { repo.mine(20, 0) } returns Result.success(sampleList())

        var callbackResult: Boolean? = null
        viewModel.complete("t1") { callbackResult = it }
        advanceUntilIdle()

        assertEquals(true, callbackResult)
    }

    @Test
    fun `clearError resets error`() = runTest {
        coEvery { repo.search(any(), any(), any(), 20, 0) } returns Result.failure(Exception("Boom"))
        viewModel.search()
        advanceUntilIdle()
        assertEquals("Boom", viewModel.error.value)

        viewModel.clearError()
        assertNull(viewModel.error.value)
    }
}