package com.example.campuslift.Components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * CampusLift error message box.
 * Shown when an action fails (network error, validation error, etc.).
 *
 * Author: Keshvir Parthab (ST10451537)
 */
@Composable
fun ErrorMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFFFFEBEE),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = message,
            color = Color(0xFFD32F2F),
            fontSize = 14.sp,
            modifier = Modifier.padding(12.dp)
        )
    }
}