package com.example.campuslift.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campuslift.Components.CampusLiftButton
import com.example.campuslift.Components.ErrorMessage
import com.example.campuslift.Components.PassiveBanner
import com.example.campuslift.Data.dto.BookingWithTripDto
import com.example.campuslift.ViewModels.BookingViewModel
import com.example.campuslift.ViewModels.TripViewModel
import kotlinx.coroutines.delay
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun LiftDetailsScreen(
    tripId: String,
    onBack: () -> Unit = {},
    onTripCancelled: () -> Unit = {},
    tripViewModel: TripViewModel = viewModel(),
    bookingViewModel: BookingViewModel = viewModel()
) {
    val trip by tripViewModel.selected.collectAsStateWithLifecycle()
    val tripBookings by bookingViewModel.tripBookings.collectAsStateWithLifecycle()
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var showCancelBanner by remember { mutableStateOf(false) }
    var approvalBannerMessage by remember { mutableStateOf("") }
    var showApprovalBanner by remember { mutableStateOf(false) }
    var showCompleteBanner by remember { mutableStateOf(false) }

    LaunchedEffect(tripId) {
        tripViewModel.loadTrip(tripId)
        bookingViewModel.loadForTrip(tripId)
    }

    LaunchedEffect(showCancelBanner) {
        if (showCancelBanner) {
            delay(1500)
            onTripCancelled()
        }
    }

    LaunchedEffect(showCompleteBanner) {
        if (showCompleteBanner) {
            delay(1500)
            onTripCancelled()
        }
    }

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

    val pendingRequests = tripBookings.filter { it.approval == "pending" }
    val approvedPassengers = tripBookings.filter { it.approval == "approved" && it.cancellationTime == null }

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
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🚗", fontSize = 24.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "You're driving",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${trip?.seatsTaken ?: 0} of ${trip?.totalSeats ?: 0} seats booked",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                val tripInProgress = approvedPassengers.any { it.pickupConfirmed }

                val statusColor = if (trip?.isComplete == true) {
                    Color(0xFFE8F5E9) to Color(0xFF2E7D32)
                } else if (tripInProgress) {
                    Color(0xFFFFF3E0) to Color(0xFFE65100)
                } else {
                    Color(0xFFE3F2FD) to Color(0xFF1565C0)
                }

                Surface(
                    color = statusColor.first,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Text(
                        text = if (trip?.isComplete == true) {
                            "•  Completed"
                        } else if (tripInProgress) {
                            "•  In Progress"
                        } else {
                            "•  ${trip?.seatsRemaining ?: 0} seats remaining"
                        },
                        fontSize = 13.sp,
                        color = statusColor.second,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                if (pendingRequests.isNotEmpty()) {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "PENDING REQUESTS (${pendingRequests.size})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            pendingRequests.forEachIndexed { index, request ->
                                PendingRequestRow(
                                    request = request,
                                    onApprove = {
                                        bookingViewModel.approveBooking(request.id, true) { success ->
                                            if (success) {
                                                bookingViewModel.loadForTrip(tripId)
                                                tripViewModel.loadTrip(tripId)
                                                approvalBannerMessage = "Request approved"
                                                showApprovalBanner = true
                                            } else {
                                                errorMessage = "Failed to approve request."
                                            }
                                        }
                                    },
                                    onDecline = {
                                        bookingViewModel.approveBooking(request.id, false) { success ->
                                            if (success) {
                                                bookingViewModel.loadForTrip(tripId)
                                                approvalBannerMessage = "Request declined"
                                                showApprovalBanner = true
                                            } else {
                                                errorMessage = "Failed to decline request."
                                            }
                                        }
                                    }
                                )
                                if (index < pendingRequests.lastIndex) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (approvedPassengers.isNotEmpty() && trip?.isComplete != true) {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "APPROVED PASSENGERS (${approvedPassengers.size})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            approvedPassengers.forEachIndexed { index, passenger ->
                                ApprovedPassengerRow(passenger = passenger)
                                if (index < approvedPassengers.lastIndex) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "TRIP DETAILS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
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
                                Text(text = "Pickup · $formattedTime", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                                Text(text = "Drop-off", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(text = formattedDate, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        if (!trip?.description.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "NOTES",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = trip?.description ?: "",
                                fontSize = 13.sp,
                                fontStyle = FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Price per seat", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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

                if (trip?.isComplete != true) {
                    val allPickupsConfirmed = approvedPassengers.isNotEmpty() && approvedPassengers.all { it.pickupConfirmed }

                    CampusLiftButton(
                        text = "Complete Trip",
                        onClick = {
                            if (allPickupsConfirmed) {
                                tripViewModel.complete(tripId) { success ->
                                    if (success) {
                                        showCompleteBanner = true
                                    } else {
                                        errorMessage = tripViewModel.error.value ?: "Failed to complete trip. Try again."
                                    }
                                }
                            } else {
                                errorMessage = "Cannot complete trip yet. Passengers must tap 'Confirm Pickup' on their app once boarded."
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            tripViewModel.cancel(tripId) { success ->
                                if (success) {
                                    showCancelBanner = true
                                } else {
                                    errorMessage = "Failed to cancel trip. Try again."
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Text("Cancel Trip", fontSize = 16.sp)
                    }
                }
            }
        }

        PassiveBanner(
            message = "Trip cancelled",
            visible = showCancelBanner,
            isSuccess = false,
            onDismiss = { showCancelBanner = false },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
        )

        PassiveBanner(
            message = "Trip completed",
            visible = showCompleteBanner,
            isSuccess = true,
            onDismiss = { showCompleteBanner = false },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
        )

        PassiveBanner(
            message = approvalBannerMessage,
            visible = showApprovalBanner,
            isSuccess = approvalBannerMessage.contains("approved"),
            onDismiss = { showApprovalBanner = false },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
        )
    }
}

@Composable
private fun PendingRequestRow(
    request: BookingWithTripDto,
    onApprove: () -> Unit,
    onDecline: () -> Unit
) {
    Column {
        Text(
            text = listOfNotNull(request.passenger?.name, request.passenger?.surname)
                .joinToString(" ")
                .ifBlank { "Passenger" },
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A237E)
        )
        Text(
            text = "${request.seatsRequested} seat${if (request.seatsRequested == 1) "" else "s"} requested",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onApprove,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                modifier = Modifier.weight(1f)
            ) {
                Text("Approve")
            }

            OutlinedButton(
                onClick = onDecline,
                modifier = Modifier.weight(1f)
            ) {
                Text("Decline")
            }
        }
    }
}

@Composable
private fun ApprovedPassengerRow(passenger: BookingWithTripDto) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = listOfNotNull(passenger.passenger?.name, passenger.passenger?.surname)
                    .joinToString(" ")
                    .ifBlank { "Passenger" },
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A237E)
            )
            Text(
                text = "${passenger.seatsRequested} seat${if (passenger.seatsRequested == 1) "" else "s"}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (passenger.pickupConfirmed) {
            Surface(
                color = Color(0xFFE8F5E9),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "✓ Picked up",
                    fontSize = 12.sp,
                    color = Color(0xFF2E7D32),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        } else {
            Surface(
                color = Color(0xFFE3F2FD),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Awaiting pickup",
                    fontSize = 12.sp,
                    color = Color(0xFF1565C0),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}