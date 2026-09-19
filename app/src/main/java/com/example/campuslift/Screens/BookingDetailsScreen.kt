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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campuslift.Components.CampusLiftButton
import com.example.campuslift.Components.ErrorMessage
import com.example.campuslift.ViewModels.BookingViewModel
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun BookingDetailsScreen(
    bookingId: String,
    onBack: () -> Unit = {},
    onBookingCancelled: () -> Unit = {},
    bookingViewModel: BookingViewModel = viewModel()
) {
    val bookings by bookingViewModel.myBookings.collectAsStateWithLifecycle()
    val booking = bookings.find { it.id == bookingId }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        bookingViewModel.loadMyBookings()
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

    val statusText = when (booking?.approval) {
        "approved" -> "Confirmed"
        "rejected" -> "Rejected"
        else -> "Pending"
    }

    val statusColor = when (booking?.approval) {
        "approved" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        "rejected" -> Color(0xFFFFEBEE) to Color(0xFFD32F2F)
        else -> Color(0xFFFFF3E0) to Color(0xFFE65100)
    }

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

            Text(
                text = "${booking?.fromLocation ?: ""} → ${booking?.toLocation ?: ""}",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${booking?.seatsRequested ?: 0} seat${if (booking?.seatsRequested == 1) "" else "s"} requested",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp
            )
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

            if (booking?.approval != "rejected" && booking?.cancellationTime == null) {
                CampusLiftButton(
                    text = "Cancel Booking",
                    onClick = {
                        bookingViewModel.cancelBooking(bookingId) { success ->
                            if (success) {
                                onBookingCancelled()
                            } else {
                                errorMessage = "Failed to cancel booking. Try again."
                            }
                        }
                    }
                )
            }
        }
    }
}