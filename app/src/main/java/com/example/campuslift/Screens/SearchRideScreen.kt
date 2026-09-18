package com.example.campuslift.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.campuslift.Components.EmptyState

@Composable
fun SearchRideScreen(
    onBack: () -> Unit = {},
    onRideSelected: () -> Unit = {}
) {
    var fromLocation by remember { mutableStateOf("") }
    var toLocation by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

    val searchResults = remember { mutableStateOf<List<String>>(emptyList()) }

    Scaffold(
        topBar = {
            CampusLiftTopBar(title = "Find a Ride", onBack = onBack)
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

            CampusLiftButton(
                text = "Search",
                onClick = {
                    searchResults.value = listOf("Gateway → UKZN, 08:00")
                }
            )

            if (searchResults.value.isEmpty()) {
                EmptyState(
                    title = "No rides found",
                    subtitle = "Try adjusting your search"
                )
            } else {
                LazyColumn {
                    items(searchResults.value) { result ->
                        Text(
                            text = result,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onRideSelected() }
                                .padding(vertical = 12.dp)
                        )
                    }
                }
            }
        }
    }
}