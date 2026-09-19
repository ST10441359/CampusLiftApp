package com.example.campuslift.Screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campuslift.Components.CampusLiftButton
import com.example.campuslift.Components.CampusLiftTextField
import com.example.campuslift.Components.CampusLiftTopBar
import com.example.campuslift.Components.LanguageDropdown
import com.example.campuslift.Components.SettingsClickRow
import com.example.campuslift.Components.SettingsToggleRow
import com.example.campuslift.Components.ValidationUtils
import com.example.campuslift.ViewModels.SettingsViewModel

/**
 * CampusLift Settings screen.
 * Toggles + language auto-save. Text fields save when Save is tapped.
 * Shows a Toast confirmation when settings are saved.
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
    Log.d(TAG, "Settings screen opened")

    val context = LocalContext.current

    // Auto-saved settings
    val darkMode by settingsViewModel.darkMode.collectAsStateWithLifecycle()
    val biometric by settingsViewModel.biometricEnabled.collectAsStateWithLifecycle()
    val notifications by settingsViewModel.notificationsEnabled.collectAsStateWithLifecycle()
    val language by settingsViewModel.language.collectAsStateWithLifecycle()

    // Persisted text values
    val savedPickup by settingsViewModel.defaultPickup.collectAsStateWithLifecycle()
    val savedContact by settingsViewModel.emergencyContact.collectAsStateWithLifecycle()
    val savedVehicle by settingsViewModel.vehicle.collectAsStateWithLifecycle()
    val savedPayment by settingsViewModel.paymentMethod.collectAsStateWithLifecycle()

    // Local editable copies — user types here, Save commits them
    var editPickup by remember { mutableStateOf("") }
    var editContact by remember { mutableStateOf("") }
    var editVehicle by remember { mutableStateOf("") }
    var editPayment by remember { mutableStateOf("") }

    // Sync local edits with persisted values when they first load
    LaunchedEffect(savedPickup) { editPickup = savedPickup }
    LaunchedEffect(savedContact) { editContact = savedContact }
    LaunchedEffect(savedVehicle) { editVehicle = savedVehicle }
    LaunchedEffect(savedPayment) { editPayment = savedPayment }

    // Validation errors
    var pickupError by remember { mutableStateOf<String?>(null) }
    var contactError by remember { mutableStateOf<String?>(null) }
    var vehicleError by remember { mutableStateOf<String?>(null) }
    var paymentError by remember { mutableStateOf<String?>(null) }

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
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "nomvula@ukzn.ac.za",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "⭐ 4.9   |   23 Trips   |   Verified Student",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "PREFERENCES",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // --- Language (auto-saves) ---
            SettingsClickRow(
                label = "Language",
                value = "",
                onClick = { },
                trailingContent = {
                    LanguageDropdown(
                        currentLanguage = language,
                        onLanguageSelected = {
                            Log.d(TAG, "Language changed to $it")
                            settingsViewModel.setLanguage(it)
                        }
                    )
                }
            )
            Divider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))

            // --- Dark Mode (auto-saves) ---
            SettingsToggleRow(
                label = "Dark Mode",
                checked = darkMode,
                onCheckedChange = {
                    Log.d(TAG, "Dark Mode toggled to $it")
                    settingsViewModel.setDarkMode(it)
                }
            )
            Divider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))

            // --- Biometric (auto-saves) ---
            SettingsToggleRow(
                label = "Biometric Login",
                checked = biometric,
                onCheckedChange = {
                    Log.d(TAG, "Biometric toggled to $it")
                    settingsViewModel.setBiometric(it)
                }
            )
            Divider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))

            // --- Notifications (auto-saves) ---
            SettingsToggleRow(
                label = "Notifications",
                checked = notifications,
                onCheckedChange = {
                    Log.d(TAG, "Notifications toggled to $it")
                    settingsViewModel.setNotifications(it)
                }
            )
            Divider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))

            // --- Default Pickup (editable field) ---
            CampusLiftTextField(
                value = editPickup,
                onValueChange = {
                    editPickup = it
                    pickupError = ValidationUtils.validateRequired(it, "Pickup location")
                },
                label = "Default Pickup",
                errorMessage = pickupError
            )

            // --- Emergency Contact (editable field) ---
            CampusLiftTextField(
                value = editContact,
                onValueChange = {
                    editContact = it
                    contactError = ValidationUtils.validatePhoneNumber(it)
                },
                label = "Emergency Contact",
                errorMessage = contactError
            )

            // --- My Vehicle (editable field) ---
            CampusLiftTextField(
                value = editVehicle,
                onValueChange = {
                    editVehicle = it
                    vehicleError = ValidationUtils.validateRequired(it, "Vehicle")
                },
                label = "My Vehicle",
                errorMessage = vehicleError
            )

            // --- Payment Method (editable field) ---
            CampusLiftTextField(
                value = editPayment,
                onValueChange = {
                    editPayment = it
                    paymentError = ValidationUtils.validateRequired(it, "Payment method")
                },
                label = "Payment Method",
                errorMessage = paymentError
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- Save button (commits the 4 editable fields) ---
            CampusLiftButton(
                text = "Save Settings",
                onClick = {
                    val e1 = ValidationUtils.validateRequired(editPickup, "Pickup location")
                    val e2 = ValidationUtils.validatePhoneNumber(editContact)
                    val e3 = ValidationUtils.validateRequired(editVehicle, "Vehicle")
                    val e4 = ValidationUtils.validateRequired(editPayment, "Payment method")

                    pickupError = e1
                    contactError = e2
                    vehicleError = e3
                    paymentError = e4

                    if (e1 == null && e2 == null && e3 == null && e4 == null) {
                        Log.d(TAG, "Saving all editable settings")
                        settingsViewModel.setDefaultPickup(editPickup)
                        settingsViewModel.setEmergencyContact(editContact)
                        settingsViewModel.setVehicle(editVehicle)
                        settingsViewModel.setPaymentMethod(editPayment)

                        Toast.makeText(context, "Settings saved", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.w(TAG, "Save blocked — fix errors first")
                        Toast.makeText(context, "Please fix the errors first", Toast.LENGTH_SHORT).show()
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            CampusLiftButton(
                text = "Sign Out",
                onClick = onSignOut
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}