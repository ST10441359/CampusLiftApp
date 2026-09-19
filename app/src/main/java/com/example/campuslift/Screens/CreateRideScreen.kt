package com.example.campuslift.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campuslift.Components.CampusLiftButton
import com.example.campuslift.Components.CampusLiftTextField
import com.example.campuslift.Components.CampusLiftTopBar
import com.example.campuslift.Components.ErrorMessage
import com.example.campuslift.Components.PassiveBanner
import com.example.campuslift.Components.ValidationUtils
import com.example.campuslift.Data.dto.CreateTripRequest
import com.example.campuslift.ViewModels.TripViewModel
import com.example.campuslift.ViewModels.VehicleViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRideScreen(
    onBack: () -> Unit = {},
    onRideCreated: () -> Unit = {},
    tripViewModel: TripViewModel = viewModel(),
    vehicleViewModel: VehicleViewModel = viewModel()
) {
    var fromLocation by remember { mutableStateOf("") }
    var toLocation by remember { mutableStateOf("") }
    var pricePerSeat by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var selectedHour by remember { mutableStateOf<Int?>(null) }
    var selectedMinute by remember { mutableStateOf<Int?>(null) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showBanner by remember { mutableStateOf(false) }

    val vehicles by vehicleViewModel.vehicles.collectAsStateWithLifecycle()
    val selectedVehicle = vehicles.firstOrNull()
    val vehicleSeats = selectedVehicle?.seats

    LaunchedEffect(Unit) {
        vehicleViewModel.loadVehicles()
    }

    LaunchedEffect(showBanner) {
        if (showBanner) {
            delay(1500)
            onRideCreated()
        }
    }

    val displayDate = selectedDateMillis?.let {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(it)
    } ?: "Tap to select"

    val displayTime = if (selectedHour != null && selectedMinute != null) {
        String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
    } else "Tap to select"

    fun buildIsoEventTime(): String? {
        val dateMillis = selectedDateMillis ?: return null
        val hour = selectedHour ?: return null
        val minute = selectedMinute ?: return null

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.timeInMillis = dateMillis
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        isoFormat.timeZone = TimeZone.getTimeZone("UTC")
        return isoFormat.format(calendar.time)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                CampusLiftTopBar(title = "Create a Ride", onBack = onBack)
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
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

                OutlinedTextField(
                    value = displayDate,
                    onValueChange = {},
                    readOnly = true,
                    shape = RoundedCornerShape(12.dp),
                    label = { Text("Date") },
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Filled.CalendarToday, contentDescription = "Pick date")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = displayTime,
                    onValueChange = {},
                    readOnly = true,
                    shape = RoundedCornerShape(12.dp),
                    label = { Text("Time") },
                    trailingIcon = {
                        IconButton(onClick = { showTimePicker = true }) {
                            Icon(Icons.Filled.AccessTime, contentDescription = "Pick time")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                CampusLiftTextField(
                    value = pricePerSeat,
                    onValueChange = { pricePerSeat = it },
                    label = "Price per seat"
                )

                if (selectedVehicle != null) {
                    Text(
                        text = "Seats available: $vehicleSeats (from your ${selectedVehicle.make} ${selectedVehicle.model})",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Text(
                        text = "No vehicle found — add a vehicle first",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                CampusLiftTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Description (optional)"
                )

                errorMessage?.let {
                    ErrorMessage(message = it)
                }

                CampusLiftButton(
                    text = "Submit",
                    onClick = {
                        errorMessage = null

                        val priceValue = pricePerSeat.toDoubleOrNull()
                        val vehicleId = selectedVehicle?.id
                        val eventTimeIso = buildIsoEventTime()

                        val validationError = ValidationUtils.validateRequired(fromLocation, "Pickup location")
                            ?: ValidationUtils.validateRequired(toLocation, "Destination")
                            ?: if (eventTimeIso == null) "Please choose a date and time" else null
                                ?: if (priceValue == null || priceValue < 0) "Enter a valid price per seat" else null
                                    ?: if (vehicleId == null || vehicleSeats == null) "No vehicle found — add a vehicle first" else null

                        if (validationError != null) {
                            errorMessage = validationError
                        } else {
                            tripViewModel.create(
                                CreateTripRequest(
                                    vehicleId = vehicleId,
                                    fromLocation = fromLocation,
                                    toLocation = toLocation,
                                    fromLat = 0.0,
                                    fromLng = 0.0,
                                    toLat = 0.0,
                                    toLng = 0.0,
                                    eventTime = eventTimeIso!!,
                                    pricePerSeat = priceValue!!,
                                    totalSeats = vehicleSeats!!,
                                    description = description.ifBlank { null }
                                )
                            ) { success ->
                                if (success) {
                                    showBanner = true
                                } else {
                                    errorMessage = "Failed to create ride. Try again."
                                }
                            }
                        }
                    }
                )
            }
        }

        PassiveBanner(
            message = "Ride published",
            visible = showBanner,
            isSuccess = true,
            onDismiss = { showBanner = false },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
        )

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        selectedDateMillis = datePickerState.selectedDateMillis
                        showDatePicker = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        if (showTimePicker) {
            val timePickerState = rememberTimePickerState(is24Hour = true)
            Dialog(onDismissRequest = { showTimePicker = false }) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = "Select time", style = MaterialTheme.typography.titleMedium)
                        TimeInput(state = timePickerState)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { showTimePicker = false }) {
                                Text("Cancel")
                            }
                            TextButton(onClick = {
                                selectedHour = timePickerState.hour
                                selectedMinute = timePickerState.minute
                                showTimePicker = false
                            }) {
                                Text("OK")
                            }
                        }
                    }
                }
            }
        }
    }
}