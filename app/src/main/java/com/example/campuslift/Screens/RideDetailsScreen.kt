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
import androidx.compose.ui.text.font.FontStyle
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
fun RideDetailsScreen(
    tripId: String,
    onBack: () -> Unit = {},
    onRequestSent: () -> Unit = {},
    tripViewModel: TripViewModel = viewModel(),
    bookingViewModel: BookingViewModel = viewModel()
) {
    val trip by tripViewModel.selected.collectAsStateWithLifecycle()
    val myBookings by bookingViewModel.myBookings.collectAsStateWithLifecycle()
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showBanner by remember { mutableStateOf(false) }

    LaunchedEffect(tripId) {
        tripViewModel.loadTrip(tripId)
        bookingViewModel.loadMyBookings()
    }

    LaunchedEffect(showBanner) {
        if (showBanner) {
            delay(1500)
            onRequestSent()
        }
    }

    val existingBooking = myBookings.find { it.tripId == tripId && it.cancellationTime == null }

    val formattedDate = trip?.eventTime?.let {
        try {
            OffsetDateTime.parse(it).format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy"))
        } catch (e: Exception) {
            it
        }
    } ?: "No date set"

    val formattedTime = trip?.eventTime?.let {
        try {
            OffsetDateTime.parse(it).format(DateTimeFormatter.ofPattern("hh:mm a"))
        } catch (e: Exception) {
            ""
        }
    } ?: ""

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
                            text = "${trip?.seatsRemaining ?: 0} of ${trip?.totalSeats ?: 0} seats available",
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
                val isFull = (trip?.seatsRemaining ?: 0) <= 0

                if (existingBooking != null) {
                    val statusColor = when (existingBooking.approval) {
                        "approved" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
                        "rejected" -> Color(0xFFFFEBEE) to Color(0xFFD32F2F)
                        else -> Color(0xFFFFF3E0) to Color(0xFFE65100)
                    }
                    val statusText = when (existingBooking.approval) {
                        "approved" -> "Your request was approved"
                        "rejected" -> "This trip was rejected by the driver"
                        else -> "Your request is pending approval"
                    }

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
                } else {
                    Surface(
                        color = if (isFull) Color(0xFFFFEBEE) else Color(0xFFE3F2FD),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = if (isFull) "•  This ride is full" else "•  ${trip?.seatsRemaining ?: 0} seats available",
                            fontSize = 13.sp,
                            color = if (isFull) Color(0xFFD32F2F) else Color(0xFF1565C0),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
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
                                Text(text = trip?.fromLocation ?: "", fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
                                Text(text = trip?.toLocation ?: "", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "Drop-off", fontSize = 12.sp, color = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(text = formattedDate, fontSize = 12.sp, color = Color.Gray)

                        if (!trip?.description.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "NOTES",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = trip?.description ?: "",
                                fontSize = 13.sp,
                                fontStyle = FontStyle.Italic,
                                color = Color.DarkGray
                            )
                        }
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
                            text = "R${trip?.pricePerSeat ?: 0}",
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

                if (existingBooking == null) {
                    CampusLiftButton(
                        text = "Request Seat",
                        onClick = {
                            errorMessage = null
                            if (isFull) {
                                errorMessage = "This ride is full."
                            } else {
                                bookingViewModel.createBooking(tripId, 1) { success ->
                                    if (success) {
                                        showBanner = true
                                    } else {
                                        errorMessage = bookingViewModel.error.value ?: "Failed to send request. Try again."
                                    }
                                }
                            }
                        }
                    )

                    Text(
                        text = "Payment held securely until trip is completed",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        PassiveBanner(
            message = "Request sent",
            visible = showBanner,
            isSuccess = true,
            onDismiss = { showBanner = false },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
        )
    }
}