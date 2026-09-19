package com.example.campuslift.Components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.campuslift.Data.dto.BookingWithTripDto
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

@Composable
fun BookingCard(
    booking: BookingWithTripDto,
    onClick: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${booking.fromLocation} → ${booking.toLocation}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A237E)
                    )
                    Text(
                        text = "${booking.seatsRequested} seat${if (booking.seatsRequested == 1) "" else "s"} requested",
                        fontSize = 13.sp,
                        color = Color.Black
                    )
                }

                Text(
                    text = "›",
                    fontSize = 18.sp,
                    color = Color.Gray
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
                    color = Color.Black
                )

                Spacer(modifier = Modifier.weight(1f))

                val statusColor = when (booking.approval) {
                    "approved" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
                    "rejected" -> Color(0xFFFFEBEE) to Color(0xFFD32F2F)
                    else -> Color(0xFFFFF3E0) to Color(0xFFE65100)
                }

                val statusText = when (booking.approval) {
                    "approved" -> "Confirmed"
                    "rejected" -> "Rejected"
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
                    color = Color.Black
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