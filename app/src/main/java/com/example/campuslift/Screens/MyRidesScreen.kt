package com.example.campuslift.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.campuslift.Components.CampusLiftRideCard
import com.example.campuslift.Components.CampusLiftTopBar
import com.example.campuslift.Components.EmptyState

data class MyRideEntry(
    val fromLocation: String,
    val toLocation: String,
    val eventTime: String,
    val role: String
)

@Composable
fun MyRidesScreen(
    rides: List<MyRideEntry> = listOf(
        MyRideEntry("Gateway", "UKZN", "Monday - 08:00", "Booked"),
        MyRideEntry("Gateway", "Umhlanga", "Tuesday - 09:00", "Driver")
    ),
    onBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CampusLiftTopBar(title = "My Rides", onBack = onBack)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (rides.isEmpty()) {
                EmptyState(
                    title = "No rides yet",
                    subtitle = "Book or offer a ride to see it here"
                )
            } else {
                LazyColumn {
                    items(rides) { ride ->
                        CampusLiftRideCard(
                            driverName = ride.role,
                            fromLocation = ride.fromLocation,
                            toLocation = ride.toLocation,
                            time = ride.eventTime,
                            price = ""
                        )
                    }
                }
            }
        }
    }
}