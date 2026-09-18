package com.example.campuslift.Screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.campuslift.Components.CampusLiftTopBar
import com.example.campuslift.Components.EmptyState

@Composable
fun AlertsScreen() {
    Scaffold(
        topBar = {
            CampusLiftTopBar(title = "Alerts")
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            EmptyState(
                title = "No alerts yet",
                subtitle = "Notifications are coming soon"
            )
        }
    }
}