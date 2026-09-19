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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campuslift.Components.CampusLiftButton
import com.example.campuslift.Components.CampusLiftRideCard
import com.example.campuslift.Components.CampusLiftTextField
import com.example.campuslift.Components.EmptyState
import com.example.campuslift.ViewModels.VehicleViewModel

data class RideResult(
    val driverInitials: String,
    val driverName: String,
    val rating: Double,
    val reviewCount: Int,
    val fromLocation: String,
    val toLocation: String,
    val time: String,
    val seatsAvailable: Int,
    val price: String
)

@Composable
fun HomeScreen(
    userName: String = "Student",
    onSignOut: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToCreateRide: () -> Unit,
    onNavigateToAddVehicle: () -> Unit,
    onNavigateToMyRides: () -> Unit,
    onRideSelected: () -> Unit = {},
    vehicleViewModel: VehicleViewModel = viewModel()
) {
    var fromLocation by remember { mutableStateOf("") }
    var toLocation by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

    val vehicles by vehicleViewModel.vehicles.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        vehicleViewModel.loadVehicles()
    }

    val allRides = remember {
        listOf(
            RideResult("TM", "Thandiwe M.", 4.8, 47, "Howard College", "Westville Campus", "07:30", 3, "R18"),
            RideResult("SD", "Sipho Dube", 4.9, 132, "PMB Campus", "Howard College", "08:00", 1, "R24"),
            RideResult("KN", "Kefilwe N.", 4.6, 29, "Howard College", "Westville Campus", "07:45", 2, "R18")
        )
    }

    val filteredRides = allRides.filter { ride ->
        (fromLocation.isBlank() || ride.fromLocation.contains(fromLocation, ignoreCase = true)) &&
                (toLocation.isBlank() || ride.toLocation.contains(toLocation, ignoreCase = true))
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
                text = userName,
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
                    CampusLiftTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = "Date (YYYY-MM-DD)"
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
                text = "${filteredRides.size} rides available",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A237E)
            )
        }

        if (filteredRides.isEmpty()) {
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
                items(filteredRides) { ride ->
                    Box(
                        modifier = Modifier.clickable { onRideSelected() }
                    ) {
                        CampusLiftRideCard(
                            driverInitials = ride.driverInitials,
                            driverName = ride.driverName,
                            rating = ride.rating,
                            reviewCount = ride.reviewCount,
                            fromLocation = ride.fromLocation,
                            toLocation = ride.toLocation,
                            time = ride.time,
                            seatsAvailable = ride.seatsAvailable,
                            price = ride.price
                        )
                    }
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
}