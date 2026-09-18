package com.example.campuslift.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.campuslift.Components.BookingCard
import com.example.campuslift.Components.CampusLiftTopBar
import com.example.campuslift.Components.EmptyState
import com.example.campuslift.Components.MyRideEntry

@Composable
fun MyRidesScreen(
    rides: List<MyRideEntry> = listOf(
        MyRideEntry("TM", "Thandiwe M.", "Howard College", "Westville Campus", "Fri, 16 Aug 2026", "07:30 AM", "Driver arriving", "R18"),
        MyRideEntry("RV", "Ruan vdW", "Westville Campus", "Howard College", "Mon, 19 Aug 2026", "07:00 AM", "Confirmed", "R20"),
        MyRideEntry("KN", "Kefilwe N.", "Howard College", "PMB Campus", "Wed, 21 Aug 2026", "06:45 AM", "Confirmed", "R35")
    ),
    onBack: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    val visibleRides = rides.filter { it.isUpcoming == (selectedTab == 0) }

    Scaffold(
        topBar = {
            CampusLiftTopBar(title = "Bookings", onBack = onBack)
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
                if (visibleRides.isEmpty()) {
                    EmptyState(
                        title = "No bookings here",
                        subtitle = "Book or offer a ride to see it here"
                    )
                } else {
                    LazyColumn {
                        items(visibleRides) { ride ->
                            BookingCard(ride)
                        }
                    }
                }
            }
        }
    }
}