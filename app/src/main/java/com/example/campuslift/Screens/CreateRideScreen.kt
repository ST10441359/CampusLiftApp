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
import androidx.compose.material3.OutlinedTextField
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
fun CreateRideScreen(
    onBack: () -> Unit = {},
    onRideCreated: () -> Unit = {}
) {
    var fromLocation by remember { mutableStateOf("") }
    var toLocation by remember { mutableStateOf("") }
    var eventTime by remember { mutableStateOf("") }
    var pricePerSeat by remember { mutableStateOf("") }
    var totalSeats by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create a Ride") },
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = fromLocation,
                onValueChange = { fromLocation = it },
                label = { Text("Pickup location") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = toLocation,
                onValueChange = { toLocation = it },
                label = { Text("Destination") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = eventTime,
                onValueChange = { eventTime = it },
                label = { Text("Departure time (YYYY-MM-DD HH:MM)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = pricePerSeat,
                onValueChange = { pricePerSeat = it },
                label = { Text("Price per seat") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = totalSeats,
                onValueChange = { totalSeats = it },
                label = { Text("Total seats") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            successMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.primary)
            }

            Button(
                onClick = {
                    errorMessage = null
                    successMessage = null

                    val priceValue = pricePerSeat.toDoubleOrNull()
                    val seatsValue = totalSeats.toIntOrNull()

                    when {
                        fromLocation.isBlank() || toLocation.isBlank() || eventTime.isBlank() -> {
                            errorMessage = "Please fill in all required fields."
                        }
                        priceValue == null || priceValue < 0 -> {
                            errorMessage = "Enter a valid price per seat."
                        }
                        seatsValue == null || seatsValue <= 0 -> {
                            errorMessage = "Enter a valid number of seats."
                        }
                        else -> {
                            successMessage = "Ride created successfully."
                            onRideCreated()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit")
            }
        }
    }
}