package com.example.campuslift.ViewModels

import com.example.campuslift.Data.dto.MarkAllReadResponse
import com.example.campuslift.Data.dto.NotificationDto
import com.example.campuslift.Data.dto.NotificationListResponse
import com.example.campuslift.Data.dto.UnreadCountResponse
import com.example.campuslift.Data.repository.NotificationRepository
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
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repo: NotificationRepository
    private lateinit var viewModel: NotificationViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repo = mockk()
        viewModel = NotificationViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun sampleNotification(id: String = "n1", read: Boolean = false) = NotificationDto(
        id = id,
        userId = "u1",
        type = "booking",
        title = "Booking confirmed",
        message = "Your seat on the 08:00 trip is confirmed",
        timestamp = "2026-01-01T07:00:00Z",
        isRead = read
    )

    private fun sampleList(vararg items: NotificationDto) = NotificationListResponse(
        items = items.toList(),
        limit = 30,
        offset = 0,
        count = items.size
    )

    @Test
    fun `load success populates notifications and clears loading`() = runTest {
        coEvery { repo.list(null) } returns Result.success(
            sampleList(sampleNotification("n1"), sampleNotification("n2"))
        )

        viewModel.load()
        advanceUntilIdle()

        assertEquals(2, viewModel.notifications.value.size)
        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun `load failure sets error`() = runTest {
        coEvery { repo.list(null) } returns Result.failure(Exception("Server error"))

        viewModel.load()
        advanceUntilIdle()

        assertEquals("Server error", viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `load unreadOnly passes through to repository`() = runTest {
        coEvery { repo.list(true) } returns Result.success(sampleList())

        viewModel.load(unreadOnly = true)
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `refreshUnreadCount updates badge count`() = runTest {
        coEvery { repo.unreadCount() } returns Result.success(UnreadCountResponse(unread = 7))

        viewModel.refreshUnreadCount()
        advanceUntilIdle()

        assertEquals(7, viewModel.unreadCount.value)
    }

    @Test
    fun `refreshUnreadCount failure leaves count unchanged`() = runTest {
        coEvery { repo.unreadCount() } returns Result.failure(Exception("Offline"))

        viewModel.refreshUnreadCount()
        advanceUntilIdle()

        assertEquals(0, viewModel.unreadCount.value)
    }

    @Test
    fun `markRead success refreshes list and unread count`() = runTest {
        coEvery { repo.markRead("n1", true) } returns Result.success(sampleNotification("n1", true))
        coEvery { repo.list(null) } returns Result.success(sampleList(sampleNotification("n1", true)))
        coEvery { repo.unreadCount() } returns Result.success(UnreadCountResponse(0))

        viewModel.markRead("n1")
        advanceUntilIdle()

        assertEquals(0, viewModel.unreadCount.value)
        assertEquals(1, viewModel.notifications.value.size)
        assertEquals(true, viewModel.notifications.value.first().isRead)
    }

    @Test
    fun `markRead failure sets error`() = runTest {
        coEvery { repo.markRead("n1", true) } returns Result.failure(Exception("Not found"))

        viewModel.markRead("n1")
        advanceUntilIdle()

        assertEquals("Not found", viewModel.error.value)
    }

    @Test
    fun `markAllRead success triggers refresh`() = runTest {
        coEvery { repo.markAllRead() } returns Result.success(MarkAllReadResponse(markedRead = 5))
        coEvery { repo.list(null) } returns Result.success(sampleList())
        coEvery { repo.unreadCount() } returns Result.success(UnreadCountResponse(0))

        viewModel.markAllRead()
        advanceUntilIdle()

        assertEquals(0, viewModel.unreadCount.value)
    }

    @Test
    fun `delete success refreshes notifications`() = runTest {
        coEvery { repo.delete("n1") } returns Result.success(Unit)
        coEvery { repo.list(null) } returns Result.success(sampleList())
        coEvery { repo.unreadCount() } returns Result.success(UnreadCountResponse(1))

        viewModel.delete("n1")
        advanceUntilIdle()

        assertEquals(1, viewModel.unreadCount.value)
    }

    @Test
    fun `delete failure sets error`() = runTest {
        coEvery { repo.delete("n1") } returns Result.failure(Exception("Permission denied"))

        viewModel.delete("n1")
        advanceUntilIdle()

        assertEquals("Permission denied", viewModel.error.value)
    }

    @Test
    fun `clearError resets error`() = runTest {
        coEvery { repo.list(null) } returns Result.failure(Exception("Boom"))
        viewModel.load()
        advanceUntilIdle()
        assertEquals("Boom", viewModel.error.value)

        viewModel.clearError()
        assertNull(viewModel.error.value)
    }
}