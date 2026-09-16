package com.example.campuslift.Screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.campuslift.Components.CampusLiftButton
import com.example.campuslift.Components.CampusLiftTopBar
import com.example.campuslift.Components.SettingsClickRow
import com.example.campuslift.Components.SettingsToggleRow

/**
 * CampusLift Settings screen.
 * Displays all user preferences with toggle switches and clickable rows.
 *
 * Author: Keshvir Parthab (ST10451537)
 */
@Composable
fun SettingsScreen() {

    // --- State (for now just local, we'll persist later) ---
    var darkMode by remember { mutableStateOf(false) }
    var biometric by remember { mutableStateOf(true) }
    var notifications by remember { mutableStateOf(true) }

    val language = "English / isiZulu"
    val emergencyContact = "+27 83 456 7890"
    val vehicle = "Toyota Etios"
    val payment = "Standard Bank"

    Column(modifier = Modifier.fillMaxSize()) {

        CampusLiftTopBar(title = "Profile / Settings")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {

            // --- Account header ---
            Text(
                text = "Nomvula Khumalo",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A237E)
            )
            Text(
                text = "nomvula@ukzn.ac.za",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "⭐ 4.9   |   23 Trips   |   Verified Student",
                fontSize = 13.sp,
                color = Color(0xFFFF6B35)
            )
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "PREFERENCES",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))

            // --- Settings rows ---
            SettingsClickRow(
                label = "Language",
                value = language,
                onClick = { /* TODO: open language picker */ }
            )
            Divider(color = Color(0xFFEEEEEE))

            SettingsToggleRow(
                label = "Dark Mode",
                checked = darkMode,
                onCheckedChange = { darkMode = it }
            )
            Divider(color = Color(0xFFEEEEEE))

            SettingsToggleRow(
                label = "Biometric Login",
                checked = biometric,
                onCheckedChange = { biometric = it }
            )
            Divider(color = Color(0xFFEEEEEE))

            SettingsToggleRow(
                label = "Notifications",
                checked = notifications,
                onCheckedChange = { notifications = it }
            )
            Divider(color = Color(0xFFEEEEEE))

            SettingsClickRow(
                label = "Emergency Contact",
                value = emergencyContact,
                onClick = { /* TODO: open contact editor */ }
            )
            Divider(color = Color(0xFFEEEEEE))

            SettingsClickRow(
                label = "My Vehicle",
                value = vehicle,
                onClick = { /* TODO: open vehicle editor */ }
            )
            Divider(color = Color(0xFFEEEEEE))

            SettingsClickRow(
                label = "Payment Methods",
                value = payment,
                onClick = { /* TODO: open payment editor */ }
            )
            Divider(color = Color(0xFFEEEEEE))

            SettingsClickRow(
                label = "Privacy & Safety",
                value = "",
                onClick = { /* TODO: open privacy page */ }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- Sign out ---
            CampusLiftButton(
                text = "Sign Out",
                onClick = { /* TODO: hook up to auth sign-out */ }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}