package com.example.campuslift.Screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import com.example.campuslift.Components.CampusLiftTopBar
import com.example.campuslift.Components.EmptyState
import com.example.campuslift.Data.dto.TripWithAvailabilityDto
import com.example.campuslift.ViewModels.TripViewModel
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun LiftsScreen(
    onBack: () -> Unit = {},
    onTripSelected: (String) -> Unit = {},
    tripViewModel: TripViewModel = viewModel()
) {
    var selectedTab by remember { mutableStateOf(0) }
    val trips by tripViewModel.trips.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        tripViewModel.loadMine()
    }

    val visibleTrips = trips.filter { trip ->
        if (selectedTab == 0) !trip.isComplete else trip.isComplete
    }

    Scaffold(
        topBar = {
            CampusLiftTopBar(title = "Lifts", onBack = onBack)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Upcoming") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Past") }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (visibleTrips.isEmpty()) {
                    EmptyState(
                        title = "No lifts here",
                        subtitle = "Create a ride to see it here"
                    )
                } else {
                    LazyColumn {
                        items(visibleTrips) { trip ->
                            LiftCard(trip = trip, onClick = { onTripSelected(trip.id) })
                        }
                    }
                }
            }
        }
    }
}

private fun formatEventDate(raw: String?): String {
    if (raw == null) return "No date set"
    return try {
        val parsed = OffsetDateTime.parse(raw)
        parsed.format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy"))
    } catch (e: Exception) {
        raw
    }
}

private fun formatEventTime(raw: String?): String {
    if (raw == null) return "No time set"
    return try {
        val parsed = OffsetDateTime.parse(raw)
        parsed.format(DateTimeFormatter.ofPattern("hh:mm a"))
    } catch (e: Exception) {
        raw
    }
}

@Composable
private fun LiftCard(trip: TripWithAvailabilityDto, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
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
                        text = "🚗",
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "You're driving",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A237E)
                    )
                    Text(
                        text = "${trip.fromLocation} → ${trip.toLocation}",
                        fontSize = 13.sp,
                        color = Color.Black
                    )
                }

                Text(
                    text = "›",
                    fontSize = 18.sp,
                    color = Color.Gray
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "\uD83D\uDCC5", fontSize = 13.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = formatEventDate(trip.eventTime),
                    fontSize = 13.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.weight(1f))

                val statusColor = if (trip.isComplete) {
                    Color(0xFFE8F5E9) to Color(0xFF2E7D32)
                } else {
                    Color(0xFFE3F2FD) to Color(0xFF1565C0)
                }

                Surface(
                    color = statusColor.first,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (trip.isComplete) "•  Completed" else "•  ${trip.seatsRemaining} of ${trip.totalSeats} seats left",
                        fontSize = 12.sp,
                        color = statusColor.second,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "\uD83D\uDD50", fontSize = 13.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = formatEventTime(trip.eventTime),
                    fontSize = 13.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "R${trip.pricePerSeat}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A237E)
                )
            }
        }
    }
}