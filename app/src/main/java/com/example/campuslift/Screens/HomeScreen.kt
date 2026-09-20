package com.example.campuslift.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import com.example.campuslift.Components.CampusLiftButton
import com.example.campuslift.Components.CampusLiftTextField
import com.example.campuslift.Components.EmptyState
import com.example.campuslift.Data.dto.TripWithAvailabilityDto
import com.example.campuslift.ViewModels.TripViewModel
import com.example.campuslift.ViewModels.UserViewModel
import com.example.campuslift.ViewModels.VehicleViewModel
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userName: String = "Student",
    onSignOut: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToCreateRide: () -> Unit,
    onNavigateToAddVehicle: () -> Unit,
    onNavigateToMyRides: () -> Unit,
    onRideSelected: (String) -> Unit = {},
    vehicleViewModel: VehicleViewModel = viewModel(),
    tripViewModel: TripViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel()
) {
    var fromLocation by remember { mutableStateOf("") }
    var toLocation by remember { mutableStateOf("") }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    val vehicles by vehicleViewModel.vehicles.collectAsStateWithLifecycle()
    val trips by tripViewModel.trips.collectAsStateWithLifecycle()
    val currentUser by userViewModel.user.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        vehicleViewModel.loadVehicles()
        tripViewModel.search()
        userViewModel.loadMe()
    }

    val otherPeoplesTrips = trips.filter { it.driverId != currentUser?.id && it.seatsRemaining > 0 }

    val displayDate = selectedDateMillis?.let {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(it)
    } ?: "Any date"

    fun buildIsoDate(): String? {
        val millis = selectedDateMillis ?: return null
        val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        isoFormat.timeZone = TimeZone.getTimeZone("UTC")
        return isoFormat.format(millis)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1A237E))
                .padding(16.dp)
        ) {
            Text(
                text = "Good morning \uD83D\uDC4B",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
            Text(
                text = currentUser?.name?.takeIf { it.isNotBlank() } ?: userName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    CampusLiftTextField(
                        value = fromLocation,
                        onValueChange = { fromLocation = it },
                        label = "From"
                    )
                    CampusLiftTextField(
                        value = toLocation,
                        onValueChange = { toLocation = it },
                        label = "To"
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

                    Spacer(modifier = Modifier.height(8.dp))

                    CampusLiftButton(
                        text = "Search",
                        onClick = {
                            tripViewModel.search(
                                from = fromLocation.ifBlank { null },
                                to = toLocation.ifBlank { null },
                                date = buildIsoDate()
                            )
                        }
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "${otherPeoplesTrips.size} rides available",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A237E)
            )
        }

        if (otherPeoplesTrips.isEmpty()) {
            EmptyState(
                title = "No rides found",
                subtitle = "Try adjusting your search"
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                items(otherPeoplesTrips) { trip ->
                    TripCard(trip = trip, onClick = { onRideSelected(trip.id) })
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            CampusLiftButton(
                text = "Create a Ride",
                onClick = {
                    if (vehicles.isEmpty()) {
                        onNavigateToAddVehicle()
                    } else {
                        onNavigateToCreateRide()
                    }
                }
            )
        }
    }

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
}

private fun formatEventTime(raw: String?): String {
    if (raw == null) return "No time set"
    return try {
        val parsed = OffsetDateTime.parse(raw)
        parsed.format(DateTimeFormatter.ofPattern("EEE, dd MMM · hh:mm a"))
    } catch (e: Exception) {
        raw
    }
}

private fun driverInitials(name: String?, surname: String?): String {
    val first = name?.trim()?.firstOrNull()?.uppercaseChar()
    val last = surname?.trim()?.firstOrNull()?.uppercaseChar()
    return listOfNotNull(first, last).joinToString("").ifBlank { "?" }
}

@Composable
private fun TripCard(trip: TripWithAvailabilityDto, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFF1A237E), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = driverInitials(trip.driver?.name, trip.driver?.surname),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = listOfNotNull(trip.driver?.name, trip.driver?.surname)
                            .joinToString(" ")
                            .ifBlank { "Driver" },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A237E)
                    )
                    if (trip.vehicle != null) {
                        Text(
                            text = listOfNotNull(trip.vehicle.color, trip.vehicle.make, trip.vehicle.model)
                                .joinToString(" "),
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                        trip.vehicle.licensePlate?.let { plate ->
                            Text(
                                text = plate,
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${trip.fromLocation} → ${trip.toLocation}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A237E)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = formatEventTime(trip.eventTime),
                fontSize = 13.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (trip.seatsRemaining <= 1) Color(0xFFFFEBEE) else Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${trip.seatsRemaining} of ${trip.totalSeats} seats",
                        fontSize = 12.sp,
                        color = if (trip.seatsRemaining <= 1) Color(0xFFD32F2F) else Color(0xFF2E7D32),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "R${trip.pricePerSeat}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A237E)
                )
            }
        }
    }
}