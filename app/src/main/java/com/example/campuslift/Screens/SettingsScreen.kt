package com.example.campuslift.Screens

import android.util.Log
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campuslift.Components.CampusLiftButton
import com.example.campuslift.Components.CampusLiftTopBar
import com.example.campuslift.Components.SettingsClickRow
import com.example.campuslift.Components.SettingsToggleRow
import com.example.campuslift.ViewModels.SettingsViewModel

/**
 * CampusLift Settings screen.
 * Preferences are read from / written to DataStore via SettingsViewModel.
 *
 * Author: Keshvir Parthab (ST10451537)
 */
@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
    onSignOut: () -> Unit = {},
    settingsViewModel: SettingsViewModel = viewModel()
) {

    val TAG = "SettingsScreen"
    Log.d(TAG, "SettingsScreen composed")

    // Observe persisted values
    val darkMode by settingsViewModel.darkMode.collectAsStateWithLifecycle()
    val biometric by settingsViewModel.biometricEnabled.collectAsStateWithLifecycle()
    val notifications by settingsViewModel.notificationsEnabled.collectAsStateWithLifecycle()
    val language by settingsViewModel.language.collectAsStateWithLifecycle()

    val languageLabel = if (language == "zu") "isiZulu" else "English"

    val emergencyContact = "+27 83 456 7890"
    val vehicle = "Toyota Etios"
    val payment = "Standard Bank"

    Column(modifier = Modifier.fillMaxSize()) {

        CampusLiftTopBar(
            title = "Profile / Settings",
            onBack = onBack
        )

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

            // --- Settings rows (now wired to DataStore) ---
            SettingsClickRow(
                label = "Language",
                value = languageLabel,
                onClick = {
                    val newLang = if (language == "zu") "en" else "zu"
                    Log.d(TAG, "Language tapped, switching to $newLang")
                    settingsViewModel.setLanguage(newLang)
                }
            )
            Divider(color = Color(0xFFEEEEEE))

            SettingsToggleRow(
                label = "Dark Mode",
                checked = darkMode,
                onCheckedChange = { settingsViewModel.setDarkMode(it) }
            )
            Divider(color = Color(0xFFEEEEEE))

            SettingsToggleRow(
                label = "Biometric Login",
                checked = biometric,
                onCheckedChange = { settingsViewModel.setBiometric(it) }
            )
            Divider(color = Color(0xFFEEEEEE))

            SettingsToggleRow(
                label = "Notifications",
                checked = notifications,
                onCheckedChange = { settingsViewModel.setNotifications(it) }
            )
            Divider(color = Color(0xFFEEEEEE))

            SettingsClickRow(
                label = "Emergency Contact",
                value = emergencyContact,
                onClick = { Log.d(TAG, "Emergency Contact tapped") }
            )
            Divider(color = Color(0xFFEEEEEE))

            SettingsClickRow(
                label = "My Vehicle",
                value = vehicle,
                onClick = { Log.d(TAG, "My Vehicle tapped") }
            )
            Divider(color = Color(0xFFEEEEEE))

            SettingsClickRow(
                label = "Payment Methods",
                value = payment,
                onClick = { Log.d(TAG, "Payment tapped") }
            )
            Divider(color = Color(0xFFEEEEEE))

            SettingsClickRow(
                label = "Privacy & Safety",
                value = "",
                onClick = { Log.d(TAG, "Privacy tapped") }
            )

            Spacer(modifier = Modifier.height(32.dp))

            CampusLiftButton(
                text = "Sign Out",
                onClick = onSignOut
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}