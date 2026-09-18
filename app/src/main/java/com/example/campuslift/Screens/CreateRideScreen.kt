package com.example.campuslift.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.campuslift.Components.CampusLiftButton
import com.example.campuslift.Components.CampusLiftTextField
import com.example.campuslift.Components.CampusLiftTopBar
import com.example.campuslift.Components.ErrorMessage
import com.example.campuslift.Components.ValidationUtils

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
            CampusLiftTopBar(title = "Create a Ride", onBack = onBack)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CampusLiftTextField(
                value = fromLocation,
                onValueChange = { fromLocation = it },
                label = "Pickup location"
            )

            CampusLiftTextField(
                value = toLocation,
                onValueChange = { toLocation = it },
                label = "Destination"
            )

            CampusLiftTextField(
                value = eventTime,
                onValueChange = { eventTime = it },
                label = "Departure time (YYYY-MM-DD HH:MM)"
            )

            CampusLiftTextField(
                value = pricePerSeat,
                onValueChange = { pricePerSeat = it },
                label = "Price per seat"
            )

            CampusLiftTextField(
                value = totalSeats,
                onValueChange = { totalSeats = it },
                label = "Total seats"
            )

            CampusLiftTextField(
                value = description,
                onValueChange = { description = it },
                label = "Description (optional)"
            )

            errorMessage?.let {
                ErrorMessage(message = it)
            }

            successMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.primary)
            }

            CampusLiftButton(
                text = "Submit",
                onClick = {
                    errorMessage = null
                    successMessage = null

                    val priceValue = pricePerSeat.toDoubleOrNull()
                    val seatsValue = totalSeats.toIntOrNull()

                    val validationError = ValidationUtils.validateRequired(fromLocation, "Pickup location")
                        ?: ValidationUtils.validateRequired(toLocation, "Destination")
                        ?: ValidationUtils.validateRequired(eventTime, "Departure time")
                        ?: if (priceValue == null || priceValue < 0) "Enter a valid price per seat" else null
                            ?: if (seatsValue == null || seatsValue <= 0) "Enter a valid number of seats" else null

                    if (validationError != null) {
                        errorMessage = validationError
                    } else {
                        successMessage = "Ride created successfully."
                        onRideCreated()
                    }
                }
            )
        }
    }
}