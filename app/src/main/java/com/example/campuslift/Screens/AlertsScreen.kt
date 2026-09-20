package com.example.campuslift.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campuslift.Components.CampusLiftTopBar
import com.example.campuslift.Components.EmptyState
import com.example.campuslift.Data.dto.NotificationDto
import com.example.campuslift.ViewModels.NotificationViewModel
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

private fun typeIcon(type: String): String = when (type) {
    "booking" -> "📋"
    "message" -> "💬"
    "rating" -> "⭐"
    else -> "🔔"
}

private fun formatTimestamp(raw: String?): String {
    if (raw == null) return ""
    return try {
        OffsetDateTime.parse(raw).format(DateTimeFormatter.ofPattern("dd MMM, hh:mm a"))
    } catch (e: Exception) {
        raw
    }
}

@Composable
fun AlertsScreen(
    notificationViewModel: NotificationViewModel = viewModel()
) {
    val notifications by notificationViewModel.notifications.collectAsStateWithLifecycle()
    val unreadCount by notificationViewModel.unreadCount.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        notificationViewModel.load()
        notificationViewModel.refreshUnreadCount()
    }

    Scaffold(
        topBar = {
            CampusLiftTopBar(title = "Alerts")
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (unreadCount > 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$unreadCount unread",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    TextButton(onClick = { notificationViewModel.markAllRead() }) {
                        Text("Mark all read")
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                if (notifications.isEmpty()) {
                    EmptyState(
                        title = "No alerts yet",
                        subtitle = "You'll see booking updates and messages here"
                    )
                } else {
                    LazyColumn {
                        items(notifications) { notification ->
                            NotificationCard(
                                notification = notification,
                                onClick = {
                                    if (!notification.isRead) {
                                        notificationViewModel.markRead(notification.id)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(notification: NotificationDto, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) Color.White else Color(0xFFE3F2FD)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFF1A237E).copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = typeIcon(notification.type), fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A237E)
                )
                Text(
                    text = notification.message,
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )
                Text(
                    text = formatTimestamp(notification.timestamp),
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            if (!notification.isRead) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFFFF6B35), CircleShape)
                )
            }
        }
    }
}