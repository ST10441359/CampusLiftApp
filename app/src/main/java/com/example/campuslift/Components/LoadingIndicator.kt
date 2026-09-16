package com.example.campuslift.Components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * CampusLift reusable loading spinner.
 * Use this whenever the app is fetching data so every screen looks consistent.
 *
 * Author: Keshvir Parthab (ST10451537)
 */
@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color(0xFF00BFA5) // CampusLift teal
        )
    }
}