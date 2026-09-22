package com.example.campuslift.ViewModels

import com.example.campuslift.Data.dto.BookingDto
import com.example.campuslift.Data.dto.BookingWithTripDto
import com.example.campuslift.Data.dto.SimpleMessageResponse
import com.example.campuslift.Data.repository.BookingRepository
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
class BookingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repo: BookingRepository
    private lateinit var viewModel: BookingViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repo = mockk()
        viewModel = BookingViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun sampleBooking(id: String = "b1") = BookingWithTripDto(
        id = id,
        tripId = "t1",
        passengerId = "u1",
        seatsRequested = 1,
        approval = "approved",
        approvedTime = null,
        bookingTime = null,
        cancellationTime = null,
        pickupConfirmed = false,
        fromLocation = "Home",
        toLocation = "Campus",
        eventTime = "2026-01-01T08:00:00Z",
        pricePerSeat = 20.0,
        passenger = null
    )

    private fun sampleBookingDto(id: String = "b1") = BookingDto(
        id = id,
        tripId = "t1",
        passengerId = "u1",
        seatsRequested = 1,
        approval = "pending",
        approvedTime = null,
        bookingTime = null,
        cancellationTime = null,
        pickupConfirmed = false
    )

    @Test
    fun `loadMyBookings success populates list and clears loading`() = runTest {
        val list = listOf(sampleBooking("b1"), sampleBooking("b2"))
        coEvery { repo.myBookings() } returns Result.success(list)

        viewModel.loadMyBookings()
        advanceUntilIdle()

        assertEquals(2, viewModel.myBookings.value.size)
        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun `loadMyBookings failure sets error and leaves list empty`() = runTest {
        coEvery { repo.myBookings() } returns Result.failure(Exception("Network error"))

        viewModel.loadMyBookings()
        advanceUntilIdle()

        assertTrue(viewModel.myBookings.value.isEmpty())
        assertEquals("Network error", viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `loadForTrip success populates tripBookings`() = runTest {
        val list = listOf(sampleBooking("b3"))
        coEvery { repo.forTrip("t1") } returns Result.success(list)

        viewModel.loadForTrip("t1")
        advanceUntilIdle()

        assertEquals(1, viewModel.tripBookings.value.size)
        assertEquals("b3", viewModel.tripBookings.value.first().id)
    }

    @Test
    fun `loadForTrip failure sets error`() = runTest {
        coEvery { repo.forTrip("t1") } returns Result.failure(Exception("Trip not found"))

        viewModel.loadForTrip("t1")
        advanceUntilIdle()

        assertEquals("Trip not found", viewModel.error.value)
        assertTrue(viewModel.tripBookings.value.isEmpty())
    }

    @Test
    fun `createBooking success invokes onDone true`() = runTest {
        coEvery { repo.create("t1", 1) } returns Result.success(sampleBookingDto())
        var callbackResult: Boolean? = null

        viewModel.createBooking("t1", 1) { callbackResult = it }
        advanceUntilIdle()

        assertEquals(true, callbackResult)
        assertNull(viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `createBooking failure sets error and invokes onDone false`() = runTest {
        coEvery { repo.create("t1", 1) } returns Result.failure(Exception("Seat not available"))
        var callbackResult: Boolean? = null

        viewModel.createBooking("t1", 1) { callbackResult = it }
        advanceUntilIdle()

        assertEquals(false, callbackResult)
        assertEquals("Seat not available", viewModel.error.value)
    }

    @Test
    fun `cancelBooking success invokes onDone true`() = runTest {
        coEvery { repo.cancel("b1") } returns Result.success(
            SimpleMessageResponse(message = "Cancelled", bookingId = "b1")
        )
        var callbackResult: Boolean? = null

        viewModel.cancelBooking("b1") { callbackResult = it }
        advanceUntilIdle()

        assertEquals(true, callbackResult)
    }

    @Test
    fun `cancelBooking failure sets error and invokes onDone false`() = runTest {
        coEvery { repo.cancel("b1") } returns Result.failure(Exception("Already cancelled"))
        var callbackResult: Boolean? = null

        viewModel.cancelBooking("b1") { callbackResult = it }
        advanceUntilIdle()

        assertEquals(false, callbackResult)
        assertEquals("Already cancelled", viewModel.error.value)
    }

    @Test
    fun `approveBooking success invokes onDone true`() = runTest {
        coEvery { repo.approve("b1", true) } returns Result.success(sampleBookingDto())
        var callbackResult: Boolean? = null

        viewModel.approveBooking("b1", true) { callbackResult = it }
        advanceUntilIdle()

        assertEquals(true, callbackResult)
    }

    @Test
    fun `approveBooking failure sets error`() = runTest {
        coEvery { repo.approve("b1", false) } returns Result.failure(Exception("Forbidden"))
        var callbackResult: Boolean? = null

        viewModel.approveBooking("b1", false) { callbackResult = it }
        advanceUntilIdle()

        assertEquals(false, callbackResult)
        assertEquals("Forbidden", viewModel.error.value)
    }

    @Test
    fun `confirmPickup success refreshes list and invokes onDone true`() = runTest {
        coEvery { repo.confirmPickup("b1") } returns Result.success(
            SimpleMessageResponse("Pickup confirmed", "b1")
        )
        coEvery { repo.myBookings() } returns Result.success(listOf(sampleBooking("b1")))

        var callbackResult: Boolean? = null
        viewModel.confirmPickup("b1") { callbackResult = it }
        advanceUntilIdle()

        assertEquals(true, callbackResult)
        assertEquals(1, viewModel.myBookings.value.size)
    }

    @Test
    fun `confirmPickup failure sets error and invokes onDone false`() = runTest {
        coEvery { repo.confirmPickup("b1") } returns Result.failure(Exception("Too early"))
        var callbackResult: Boolean? = null

        viewModel.confirmPickup("b1") { callbackResult = it }
        advanceUntilIdle()

        assertEquals(false, callbackResult)
        assertEquals("Too early", viewModel.error.value)
    }

    @Test
    fun `clearError resets error state`() = runTest {
        coEvery { repo.myBookings() } returns Result.failure(Exception("Boom"))
        viewModel.loadMyBookings()
        advanceUntilIdle()
        assertEquals("Boom", viewModel.error.value)

        viewModel.clearError()
        assertNull(viewModel.error.value)
    }
}