package com.example.campuslift.Screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campuslift.ViewModels.BookingViewModel
import androidx.compose.foundation.layout.width
@Composable
fun MyBookingsScreen(
    onBack: () -> Unit,
    vm: BookingViewModel = viewModel()
) {
    val bookings by vm.myBookings.collectAsState()
    val loading by vm.isLoading.collectAsState()
    val error by vm.error.collectAsState()

    LaunchedEffect(Unit) { vm.loadMyBookings() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("← Back") }
            Spacer(Modifier.width(8.dp))
            Text("My Bookings", style = MaterialTheme.typography.titleLarge)
        }
        Spacer(Modifier.height(8.dp))

        if (loading) CircularProgressIndicator()
        error?.let { Text("Error: $it") }

        LazyColumn {
            items(bookings) { b ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text("${b.fromLocation} → ${b.toLocation}")
                        Text("Status: ${b.approval}")
                        Text("Seats: ${b.seatsRequested}")
                        Text("R${b.pricePerSeat} / seat")
                        if (b.cancellationTime == null) {
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { vm.cancelBooking(b.id) }) {
                                Text("Cancel")
                            }
                        }
                    }
                }
            }
        }
    }
}