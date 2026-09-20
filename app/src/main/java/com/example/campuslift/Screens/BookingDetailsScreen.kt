package com.example.campuslift.Screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import com.example.campuslift.Components.CampusLiftButton
import com.example.campuslift.Components.ErrorMessage
import com.example.campuslift.Components.PassiveBanner
import com.example.campuslift.ViewModels.BookingViewModel
import com.example.campuslift.ViewModels.TripViewModel
import kotlinx.coroutines.delay
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

private fun initials(name: String?, surname: String?): String {
    val first = name?.trim()?.firstOrNull()?.uppercaseChar()
    val last = surname?.trim()?.firstOrNull()?.uppercaseChar()
    return listOfNotNull(first, last).joinToString("").ifBlank { "?" }
}

@Composable
fun BookingDetailsScreen(
    bookingId: String,
    onBack: () -> Unit = {},
    onBookingCancelled: () -> Unit = {},
    bookingViewModel: BookingViewModel = viewModel(),
    tripViewModel: TripViewModel = viewModel()
) {
    val bookings by bookingViewModel.myBookings.collectAsStateWithLifecycle()
    val booking = bookings.find { it.id == bookingId }
    val trip by tripViewModel.selected.collectAsStateWithLifecycle()
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showBanner by remember { mutableStateOf(false) }
    var showPickupBanner by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        bookingViewModel.loadMyBookings()
    }

    LaunchedEffect(booking?.tripId) {
        booking?.tripId?.let { tripViewModel.loadTrip(it) }
    }

    LaunchedEffect(showBanner) {
        if (showBanner) {
            delay(1500)
            onBookingCancelled()
        }
    }

    LaunchedEffect(showPickupBanner) {
        if (showPickupBanner) {
            delay(1500)
            showPickupBanner = false
        }
    }

    val formattedDate = booking?.eventTime?.let {
        try {
            OffsetDateTime.parse(it).format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy"))
        } catch (e: Exception) {
            it
        }
    } ?: "No date set"

    val formattedTime = booking?.eventTime?.let {
        try {
            OffsetDateTime.parse(it).format(DateTimeFormatter.ofPattern("hh:mm a"))
        } catch (e: Exception) {
            ""
        }
    } ?: ""

    val statusText = when {
        booking?.pickupConfirmed == true -> "Pickup Confirmed"
        booking?.approval == "approved" -> "Confirmed"
        booking?.approval == "rejected" -> "Rejected"
        else -> "Pending"
    }

    val statusColor = when {
        booking?.pickupConfirmed == true -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        booking?.approval == "approved" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        booking?.approval == "rejected" -> Color(0xFFFFEBEE) to Color(0xFFD32F2F)
        else -> Color(0xFFFFF3E0) to Color(0xFFE65100)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1A237E))
                    .padding(16.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val driver = trip?.driver
                    val vehicle = trip?.vehicle

                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials(driver?.name, driver?.surname),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = listOfNotNull(driver?.name, driver?.surname)
                                .joinToString(" ")
                                .ifBlank { "Driver" },
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (vehicle != null) {
                            Text(
                                text = listOfNotNull(vehicle.color, vehicle.make, vehicle.model)
                                    .joinToString(" ") + (vehicle.licensePlate?.let { " · $it" } ?: ""),
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            text = "${booking?.fromLocation ?: ""} → ${booking?.toLocation ?: ""}",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Surface(
                    color = statusColor.first,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Text(
                        text = "•  $statusText",
                        fontSize = 13.sp,
                        color = statusColor.second,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "TRIP DETAILS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(Color(0xFF1A237E), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = booking?.fromLocation ?: "", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "Pickup · $formattedTime", fontSize = 12.sp, color = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(Color(0xFFFF6B35), RoundedCornerShape(2.dp))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = booking?.toLocation ?: "", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "Drop-off", fontSize = 12.sp, color = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(text = formattedDate, fontSize = 12.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Price per seat", fontSize = 13.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "R${booking?.pricePerSeat ?: 0}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A237E)
                        )
                    }
                }

                errorMessage?.let {
                    Spacer(modifier = Modifier.height(12.dp))
                    ErrorMessage(message = it)
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (booking?.approval == "approved" && booking.pickupConfirmed == false) {
                    CampusLiftButton(
                        text = "Confirm Pickup",
                        onClick = {
                            bookingViewModel.confirmPickup(bookingId) { success ->
                                if (success) {
                                    showPickupBanner = true
                                } else {
                                    errorMessage = "Failed to confirm pickup."
                                }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (booking?.approval != "rejected" && booking?.cancellationTime == null && booking?.pickupConfirmed != true) {
                    OutlinedButton(
                        onClick = {
                            bookingViewModel.cancelBooking(bookingId) { success ->
                                if (success) {
                                    showBanner = true
                                } else {
                                    errorMessage = "Failed to cancel booking. Try again."
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Text("Cancel Booking", fontSize = 16.sp)
                    }
                }
            }
        }

        PassiveBanner(
            message = "Booking cancelled",
            visible = showBanner,
            isSuccess = false,
            onDismiss = { showBanner = false },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
        )

        PassiveBanner(
            message = "Pickup confirmed",
            visible = showPickupBanner,
            isSuccess = true,
            onDismiss = { showPickupBanner = false },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
        )
    }
}