package com.example.campuslift.Components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * CampusLift primary button.
 * Orange, rounded, full-width — matches the design in the Part 1 mockups.
 *
 * Author: Keshvir Parthab (ST10451537)
 */
@Composable
fun CampusLiftButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFF6B35), // CampusLift orange
            contentColor = Color.White
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp
        )
    }
}