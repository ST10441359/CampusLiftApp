package com.example.campuslift.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campuslift.Components.BookingCard
import com.example.campuslift.Components.CampusLiftTopBar
import com.example.campuslift.Components.EmptyState
import com.example.campuslift.Data.repository.TripRepository
import com.example.campuslift.ViewModels.BookingViewModel

@Composable
fun MyBookingsScreen(
    onBack: () -> Unit = {},
    onBookingSelected: (String) -> Unit = {},
    bookingViewModel: BookingViewModel = viewModel()
) {
    var selectedTab by remember { mutableStateOf(0) }
    val bookings by bookingViewModel.myBookings.collectAsStateWithLifecycle()

    var completedTripIds by remember { mutableStateOf(setOf<String>()) }

    LaunchedEffect(Unit) {
        bookingViewModel.loadMyBookings()
    }

    LaunchedEffect(bookings) {
        val tripRepo = TripRepository()
        val ids = mutableSetOf<String>()
        bookings.forEach { booking ->
            if (booking.cancellationTime == null && booking.approval != "rejected") {
                tripRepo.get(booking.tripId).getOrNull()?.let { trip ->
                    if (trip.isComplete) ids.add(booking.tripId)
                }
            }
        }
        completedTripIds = ids
    }

    val visibleBookings = bookings.filter { booking ->
        if (booking.cancellationTime != null) return@filter false

        val isRejected = booking.approval == "rejected"
        val isCompleted = completedTripIds.contains(booking.tripId)

        if (selectedTab == 0) !isRejected && !isCompleted
        else isRejected || isCompleted
    }

    Scaffold(
        topBar = {
            CampusLiftTopBar(title = "Bookings", onBack = onBack)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Upcoming") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Past") }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (visibleBookings.isEmpty()) {
                    EmptyState(
                        title = "No bookings here",
                        subtitle = "Book or offer a ride to see it here"
                    )
                } else {
                    LazyColumn {
                        items(visibleBookings) { booking ->
                            BookingCard(booking, onClick = { onBookingSelected(booking.id) })
                        }
                    }
                }
            }
        }
    }
}