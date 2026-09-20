package com.example.campuslift.Components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campuslift.Data.repository.BookingRepository
import com.example.campuslift.Data.repository.TripRepository
import com.example.campuslift.ViewModels.BookingViewModel
import com.example.campuslift.ViewModels.TripViewModel

private data class ActiveTrip(
    val tripId: String,
    val fromLocation: String,
    val toLocation: String,
    val isDriver: Boolean
)

@Composable
fun ActiveTripBanner(
    tripViewModel: TripViewModel = viewModel(),
    bookingViewModel: BookingViewModel = viewModel()
) {
    val myTrips by tripViewModel.trips.collectAsStateWithLifecycle()
    val myBookings by bookingViewModel.myBookings.collectAsStateWithLifecycle()

    var activeTrip by remember { mutableStateOf<ActiveTrip?>(null) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        tripViewModel.loadMine()
        bookingViewModel.loadMyBookings()
    }

    LaunchedEffect(myTrips, myBookings) {
        val tripRepo = TripRepository()
        val bookingRepo = BookingRepository()

        val passengerMatch = myBookings.firstOrNull { it.pickupConfirmed && it.cancellationTime == null }
        if (passengerMatch != null) {
            val trip = tripRepo.get(passengerMatch.tripId).getOrNull()
            if (trip != null && !trip.isComplete) {
                activeTrip = ActiveTrip(
                    tripId = passengerMatch.tripId,
                    fromLocation = passengerMatch.fromLocation,
                    toLocation = passengerMatch.toLocation,
                    isDriver = false
                )
                return@LaunchedEffect
            }
        }

        val candidateTrips = myTrips.filter { it.isActive && !it.isComplete }
        for (trip in candidateTrips) {
            val bookings = bookingRepo.forTrip(trip.id).getOrNull() ?: continue
            val hasPickedUp = bookings.any { it.pickupConfirmed && it.cancellationTime == null }
            if (hasPickedUp) {
                activeTrip = ActiveTrip(
                    tripId = trip.id,
                    fromLocation = trip.fromLocation,
                    toLocation = trip.toLocation,
                    isDriver = true
                )
                return@LaunchedEffect
            }
        }

        activeTrip = null
    }

    activeTrip?.let { trip ->
        Surface(
            color = Color(0xFF1A237E),
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .animateContentSize()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🚗", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Trip in progress",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = if (expanded) "▲" else "▼",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }

                if (expanded) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${trip.fromLocation} → ${trip.toLocation}",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp
                    )
                    Text(
                        text = if (trip.isDriver) "You're driving" else "You're a passenger",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}