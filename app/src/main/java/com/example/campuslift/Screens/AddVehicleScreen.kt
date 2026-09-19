package com.example.campuslift.Screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campuslift.Components.CampusLiftButton
import com.example.campuslift.Components.CampusLiftTextField
import com.example.campuslift.Components.CampusLiftTopBar
import com.example.campuslift.Components.ErrorMessage
import com.example.campuslift.Components.ValidationUtils
import com.example.campuslift.Data.dto.CreateVehicleRequest
import com.example.campuslift.ViewModels.VehicleViewModel
import androidx.compose.foundation.layout.width

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVehicleScreen(
    onBack: () -> Unit = {},
    onContinueToCreateRide: () -> Unit = {},
    onBackToHome: () -> Unit = {},
    vehicleViewModel: VehicleViewModel = viewModel()
) {
    var make by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var licensePlate by remember { mutableStateOf("") }
    var seats by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var vehicleSaved by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            if (vehicleSaved) {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = onBackToHome) {
                            Icon(Icons.Filled.Close, contentDescription = "Close")
                        }
                    }
                )
            } else {
                CampusLiftTopBar(title = "Add Your Vehicle", onBack = onBack)
            }
        }
    ) { padding ->
        if (vehicleSaved) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color(0xFFE8F5E9), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Success",
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    text = "Vehicle saved!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF374151)
                )
                Text(
                    text = "You're all set to start offering rides.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF374151)
                )

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$year $make $model",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A237E),
                                modifier = Modifier.weight(1f)
                            )

                            TextButton(onClick = { vehicleSaved = false }) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = "Edit",
                                    tint = Color(0xFFFF6B35),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Edit", color = Color(0xFFFF6B35))
                            }
                        }

                        Text(
                            text = "License Plate: $licensePlate • $seats Seats",
                            fontSize = 13.sp,
                            color = Color(0xFF374151)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                CampusLiftButton(
                    text = "Continue to Create Ride",
                    onClick = onContinueToCreateRide
                )

                OutlinedButton(
                    onClick = onBackToHome,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFFF6B35)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF6B35)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(text = "Back to Home", fontSize = 16.sp)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CampusLiftTextField(
                    value = make,
                    onValueChange = { make = it },
                    label = "Make (e.g. Toyota)"
                )

                CampusLiftTextField(
                    value = model,
                    onValueChange = { model = it },
                    label = "Model (e.g. Etios)"
                )

                CampusLiftTextField(
                    value = year,
                    onValueChange = { year = it },
                    label = "Year"
                )

                CampusLiftTextField(
                    value = color,
                    onValueChange = { color = it },
                    label = "Color"
                )

                CampusLiftTextField(
                    value = licensePlate,
                    onValueChange = { licensePlate = it },
                    label = "License Plate"
                )

                CampusLiftTextField(
                    value = seats,
                    onValueChange = { seats = it },
                    label = "Seats"
                )

                errorMessage?.let {
                    ErrorMessage(message = it)
                }

                CampusLiftButton(
                    text = "Save Vehicle",
                    onClick = {
                        errorMessage = null

                        val yearValue = year.toIntOrNull()
                        val seatsValue = seats.toIntOrNull()

                        val validationError = ValidationUtils.validateRequired(make, "Make")
                            ?: ValidationUtils.validateRequired(model, "Model")
                            ?: ValidationUtils.validateRequired(licensePlate, "License plate")
                            ?: if (seatsValue == null || seatsValue <= 0) "Enter a valid number of seats" else null

                        if (validationError != null) {
                            errorMessage = validationError
                        } else {
                            vehicleViewModel.create(
                                CreateVehicleRequest(
                                    make = make,
                                    model = model,
                                    year = yearValue,
                                    color = color.ifBlank { null },
                                    licensePlate = licensePlate,
                                    seats = seatsValue!!
                                )
                            ) { success ->
                                if (success) {
                                    vehicleSaved = true
                                } else {
                                    errorMessage = "Failed to save vehicle. Try again."
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}