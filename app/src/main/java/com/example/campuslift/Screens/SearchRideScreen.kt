package com.example.campuslift.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SearchRideScreen() {
    var fromLocation by remember { mutableStateOf("") }
    var toLocation by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

    val searchResults = remember { mutableStateOf<List<String>>(emptyList()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Find a Ride",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = fromLocation,
            onValueChange = { fromLocation = it },
            label = { Text("From") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = toLocation,
            onValueChange = { toLocation = it },
            label = { Text("To") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Date (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                searchResults.value = emptyList()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Search")
        }

        if (searchResults.value.isEmpty()) {
            Text("No rides found yet. Try a search.")
        } else {
            LazyColumn {
                items(searchResults.value) { result ->
                    Text(result)
                }
            }
        }
    }
}