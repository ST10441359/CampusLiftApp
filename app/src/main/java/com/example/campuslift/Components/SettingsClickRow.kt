package com.example.campuslift.Components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * CampusLift settings row that the user can tap.
 * Example: "Language → English / isiZulu", "Emergency Contact → +27 83..."
 *
 * Author: Keshvir Parthab (ST10451537)
 */
@Composable
fun SettingsClickRow(
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            color = Color(0xFF1A237E),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color.Gray
        )
        Text(
            text = "  ▸",
            fontSize = 16.sp,
            color = Color.Gray
        )
    }
}