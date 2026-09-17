package com.example.campuslift.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideDetailsScreen(
    fromLocation: String = "Gateway",
    toLocation: String = "UKZN",
    driverName: String = "John",
    eventTime: String = "Monday - 08:00",
    availableSeats: Int = 3,
    onBack: () -> Unit = {},
    onBookingConfirmed: () -> Unit = {}
) {
    var seatsLeft by remember { mutableStateOf(availableSeats) }
    var bookingMessage by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ride Details") },
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
            Text(text = "$fromLocation → $toLocation", style = MaterialTheme.typography.titleLarge)
            Text(text = "Driver: $driverName", style = MaterialTheme.typography.bodyLarge)
            Text(text = eventTime, style = MaterialTheme.typography.bodyLarge)
            Text(text = "Available Seats: $seatsLeft", style = MaterialTheme.typography.bodyLarge)

            bookingMessage?.let {
                Text(
                    text = it,
                    color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }

            Button(
                onClick = {
                    if (seatsLeft <= 0) {
                        bookingMessage = "This ride is full."
                        isError = true
                    } else {
                        seatsLeft -= 1
                        bookingMessage = "Booking confirmed."
                        isError = false
                        onBookingConfirmed()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Book Seat")
            }
        }
    }
}