package com.example.campuslift.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class MyRideEntry(
    val fromLocation: String,
    val toLocation: String,
    val eventTime: String,
    val role: String
)

@OptIn(ExperimentalMaterial3Api::class)
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
            TopAppBar(
                title = { Text("My Rides") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "Upcoming", style = MaterialTheme.typography.titleMedium)

            if (rides.isEmpty()) {
                Text("No rides yet.")
            } else {
                LazyColumn {
                    items(rides) { ride ->
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            Text(
                                text = "${ride.fromLocation} → ${ride.toLocation}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = ride.eventTime,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = ride.role,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}