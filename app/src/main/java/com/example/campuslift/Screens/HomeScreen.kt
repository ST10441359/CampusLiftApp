package com.example.campuslift.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Placeholder Home screen.
 * Will be replaced by Ziyaad's ride functionality in a future phase.
 *
 * Keshvir added: onNavigateToSettings callback so the user can reach Settings.
 */
@Composable
fun HomeScreen(
    onSignOut: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToCreateRide: () -> Unit,
    onNavigateToSearchRide: () -> Unit,
    onNavigateToMyRides: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to CampusLift",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(Modifier.height(8.dp))

        Button(onClick = onNavigateToCreateRide) {
            Text("Create a Ride")
        }
        Spacer(Modifier.height(12.dp))

        Button(onClick = onNavigateToSearchRide) {
            Text("Search Rides")
        }
        Spacer(Modifier.height(12.dp))

        Text(
            text = "Ride features coming soon.",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(24.dp))

        Button(onClick = onNavigateToSettings) {
            Text("Open Settings")
        }
        Spacer(Modifier.height(12.dp))

        Button(onClick = onNavigateToMyRides) {
            Text("My Rides")
        }
        Spacer(Modifier.height(12.dp))

        Button(onClick = onSignOut) {
            Text("Sign Out")
        }
    }
}