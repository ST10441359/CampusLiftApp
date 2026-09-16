package com.example.campuslift.Components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * CampusLift empty state — shown when a list has no items.
 * Example: "No rides found" on the search screen.
 *
 * Author: Keshvir Parthab (ST10451537)
 */
@Composable
fun EmptyState(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A237E)
        )
        Text(
            text = subtitle,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}