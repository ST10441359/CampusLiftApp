package com.example.campuslift.Components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * CampusLift top app bar — consistent header across screens.
 *
 * Author: Keshvir Parthab (ST10451537)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampusLiftTopBar(title: String) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF1A237E),
            titleContentColor = Color.White
        )
    )
}