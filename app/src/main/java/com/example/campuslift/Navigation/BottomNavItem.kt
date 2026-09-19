package com.example.campuslift.Navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "Home", Icons.Filled.Home)
    object Bookings : BottomNavItem("myRides", "Bookings", Icons.AutoMirrored.Filled.List)
    object Lifts : BottomNavItem("lifts", "Lifts", Icons.Filled.DirectionsCar)
    object Alerts : BottomNavItem("alerts", "Alerts", Icons.Filled.Notifications)
    object Profile : BottomNavItem("settings", "Profile", Icons.Filled.Person)
}