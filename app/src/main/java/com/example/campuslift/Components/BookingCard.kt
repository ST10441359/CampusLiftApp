package com.example.campuslift.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import com.example.campuslift.Data.dto.BookingWithTripDto
import com.example.campuslift.Data.dto.TripWithAvailabilityDto
import com.example.campuslift.Data.repository.TripRepository
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

private fun formatBookingDate(raw: String?): String {
    if (raw == null) return "No date set"
    return try {
        OffsetDateTime.parse(raw).format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy"))
    } catch (e: Exception) {
        raw
    }
}

private fun formatBookingTime(raw: String?): String {
    if (raw == null) return ""
    return try {
        OffsetDateTime.parse(raw).format(DateTimeFormatter.ofPattern("hh:mm a"))
    } catch (e: Exception) {
        ""
    }
}

private fun initials(name: String?, surname: String?): String {
    val first = name?.trim()?.firstOrNull()?.uppercaseChar()
    val last = surname?.trim()?.firstOrNull()?.uppercaseChar()
    return listOfNotNull(first, last).joinToString("").ifBlank { "?" }
}

@Composable
fun BookingCard(
    booking: BookingWithTripDto,
    onClick: () -> Unit = {}
) {
    var trip by remember(booking.tripId) { mutableStateOf<TripWithAvailabilityDto?>(null) }

    LaunchedEffect(booking.tripId) {
        TripRepository().get(booking.tripId)
            .onSuccess { trip = it }
    }

    val driver = trip?.driver
    val vehicle = trip?.vehicle

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() }
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
                        text = initials(driver?.name, driver?.surname),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = listOfNotNull(driver?.name, driver?.surname)
                            .joinToString(" ")
                            .ifBlank { "Driver" },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A237E)                  // kept – brand navy
                    )
                    Text(
                        text = "${booking.fromLocation} → ${booking.toLocation}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (vehicle != null) {
                        Text(
                            text = listOfNotNull(vehicle.color, vehicle.make, vehicle.model)
                                .joinToString(" ") + (vehicle.licensePlate?.let { " · $it" } ?: ""),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = "›",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📅 ${formatBookingDate(booking.eventTime)}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.weight(1f))

                val statusColor = when (booking.approval) {
                    "approved" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
                    "rejected" -> Color(0xFFFFEBEE) to Color(0xFFD32F2F)
                    else -> Color(0xFFFFF3E0) to Color(0xFFE65100)
                }

                val statusText = when {
                    trip?.isComplete == true -> "Completed"
                    booking.pickupConfirmed -> "Pickup Confirmed"
                    booking.approval == "approved" -> "Confirmed"
                    booking.approval == "rejected" -> "Rejected"
                    else -> "Pending"
                }

                Surface(
                    color = statusColor.first,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "•  $statusText",
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
                Text(
                    text = "🕐 ${formatBookingTime(booking.eventTime)}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "R${booking.pricePerSeat}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A237E)
                )
            }
        }
    }
}