package com.example.campuslift.Components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * CampusLift ride card — shows driver, route, and price.
 * Used on Home/Search results, Bookings, and anywhere else a ride is listed.
 *
 * Author: Keshvir Parthab (ST10451537)
 */
@Composable
fun CampusLiftRideCard(
    driverName: String,
    fromLocation: String,
    toLocation: String,
    time: String,
    price: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = driverName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A237E)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$fromLocation → $toLocation",
                fontSize = 14.sp,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.width(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = time,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = price,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF6B35)
                )
            }
        }
    }
}