package com.example.campuslift.Screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.campuslift.Auth.GoogleSignInHelper
import com.example.campuslift.Components.CampusLiftButton
import com.example.campuslift.Components.CampusLiftTextField
import com.example.campuslift.Components.CampusLiftTopBar
import com.example.campuslift.Components.LanguageDropdown
import com.example.campuslift.Components.SettingsClickRow
import com.example.campuslift.Components.SettingsToggleRow
import com.example.campuslift.Components.ValidationUtils
import com.example.campuslift.Data.dto.UpdateVehicleRequest
import com.example.campuslift.ViewModels.AuthViewModel
import com.example.campuslift.ViewModels.SettingsViewModel
import com.example.campuslift.ViewModels.UserViewModel
import com.example.campuslift.ViewModels.VehicleViewModel

/**
 * Profile / Settings screen.
 *
 * Sign-out goes through AuthViewModel.signOut(helper), which:
 *   1. Clears Credential Manager state (prevents stale grants — fixes the
 *      intermittent "No credentials" error after sign-out)
 *   2. Signs out of Firebase
 *
 * The helper is constructed from LocalContext.current (Activity context) and
 * remembered for the screen lifetime — Credential Manager requires it.
 */
@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
    settingsViewModel: SettingsViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel(),
    vehicleViewModel: VehicleViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current

    // Required for Credential Manager — must use Activity context.
    val googleHelper = remember { GoogleSignInHelper(context) }

    val currentUser by userViewModel.user.collectAsStateWithLifecycle()
    val vehicles by vehicleViewModel.vehicles.collectAsStateWithLifecycle()
    val myVehicle = vehicles.firstOrNull()

    LaunchedEffect(Unit) {
        userViewModel.loadMe()
        vehicleViewModel.loadVehicles()
    }

    val darkMode by settingsViewModel.darkMode.collectAsStateWithLifecycle()
    val biometric by settingsViewModel.biometricEnabled.collectAsStateWithLifecycle()
    val notifications by settingsViewModel.notificationsEnabled.collectAsStateWithLifecycle()
    val language by settingsViewModel.language.collectAsStateWithLifecycle()

    val savedPickup by settingsViewModel.defaultPickup.collectAsStateWithLifecycle()
    val savedContact by settingsViewModel.emergencyContact.collectAsStateWithLifecycle()

    var editPickup by remember { mutableStateOf("") }
    var editContact by remember { mutableStateOf("") }

    LaunchedEffect(savedPickup) { editPickup = savedPickup }
    LaunchedEffect(savedContact) { editContact = savedContact }

    var pickupError by remember { mutableStateOf<String?>(null) }
    var contactError by remember { mutableStateOf<String?>(null) }

    var editMake by remember { mutableStateOf("") }
    var editModel by remember { mutableStateOf("") }
    var editYear by remember { mutableStateOf("") }
    var editColor by remember { mutableStateOf("") }
    var editPlate by remember { mutableStateOf("") }
    var editSeats by remember { mutableStateOf("") }

    LaunchedEffect(myVehicle) {
        editMake = myVehicle?.make ?: ""
        editModel = myVehicle?.model ?: ""
        editYear = myVehicle?.year?.toString() ?: ""
        editColor = myVehicle?.color ?: ""
        editPlate = myVehicle?.licensePlate ?: ""
        editSeats = myVehicle?.seats?.toString() ?: ""
    }

    var vehicleError by remember { mutableStateOf<String?>(null) }
    var isSigningOut by remember { mutableStateOf(false) }

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

            Text(
                text = listOfNotNull(currentUser?.name, currentUser?.surname)
                    .joinToString(" ")
                    .ifBlank { "Student" },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = currentUser?.email ?: "",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "PREFERENCES",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(8.dp))

            SettingsClickRow(
                label = "Language",
                value = "",
                onClick = { },
                trailingContent = {
                    LanguageDropdown(
                        currentLanguage = language,
                        onLanguageSelected = { settingsViewModel.setLanguage(it) }
                    )
                }
            )
            Divider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))

            SettingsToggleRow(
                label = "Dark Mode",
                checked = darkMode,
                onCheckedChange = { settingsViewModel.setDarkMode(it) }
            )
            Divider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))

            SettingsToggleRow(
                label = "Biometric Login",
                checked = biometric,
                onCheckedChange = { settingsViewModel.setBiometric(it) }
            )
            Divider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))

            SettingsToggleRow(
                label = "Notifications",
                checked = notifications,
                onCheckedChange = { settingsViewModel.setNotifications(it) }
            )
            Divider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))

            CampusLiftTextField(
                value = editPickup,
                onValueChange = {
                    editPickup = it
                    pickupError = ValidationUtils.validateRequired(it, "Pickup location")
                },
                label = "Default Pickup",
                errorMessage = pickupError
            )

            CampusLiftTextField(
                value = editContact,
                onValueChange = {
                    editContact = it
                    contactError = ValidationUtils.validatePhoneNumber(it)
                },
                label = "Emergency Contact",
                errorMessage = contactError
            )

            SettingsClickRow(
                label = "Payment Method",
                value = "Cash",
                onClick = { }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "MY VEHICLE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (myVehicle == null) {
                Text(
                    text = "No vehicle added yet.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            } else {
                CampusLiftTextField(
                    value = editMake,
                    onValueChange = { editMake = it },
                    label = "Make"
                )
                CampusLiftTextField(
                    value = editModel,
                    onValueChange = { editModel = it },
                    label = "Model"
                )
                CampusLiftTextField(
                    value = editYear,
                    onValueChange = { editYear = it },
                    label = "Year"
                )
                CampusLiftTextField(
                    value = editColor,
                    onValueChange = { editColor = it },
                    label = "Color"
                )
                CampusLiftTextField(
                    value = editPlate,
                    onValueChange = { editPlate = it },
                    label = "License Plate"
                )
                CampusLiftTextField(
                    value = editSeats,
                    onValueChange = { editSeats = it },
                    label = "Seats",
                    errorMessage = vehicleError
                )

                Spacer(modifier = Modifier.height(12.dp))

                CampusLiftButton(
                    text = "Save Vehicle",
                    onClick = {
                        val seatsInt = editSeats.toIntOrNull()
                        val yearInt = editYear.toIntOrNull()

                        if (seatsInt == null || seatsInt <= 0) {
                            vehicleError = "Enter a valid number of seats"
                        } else {
                            vehicleError = null
                            vehicleViewModel.update(
                                myVehicle.id,
                                UpdateVehicleRequest(
                                    make = editMake,
                                    model = editModel,
                                    year = yearInt,
                                    color = editColor,
                                    licensePlate = editPlate,
                                    seats = seatsInt
                                )
                            ) { success ->
                                if (success) {
                                    Toast.makeText(context, "Vehicle updated", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Failed to update vehicle", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            CampusLiftButton(
                text = "Save Settings",
                onClick = {
                    val e1 = ValidationUtils.validateRequired(editPickup, "Pickup location")
                    val e2 = ValidationUtils.validatePhoneNumber(editContact)

                    pickupError = e1
                    contactError = e2

                    if (e1 == null && e2 == null) {
                        settingsViewModel.setDefaultPickup(editPickup)
                        settingsViewModel.setEmergencyContact(editContact)
                        Toast.makeText(context, "Settings saved", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Please fix the errors first", Toast.LENGTH_SHORT).show()
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Sign Out — disabled during async cleanup to prevent double-taps.
            // AuthViewModel.signOut(googleHelper) is fire-and-forget on its own
            // viewModelScope, so no local coroutine scope is needed here.
            // The authState flow will emit Unauthenticated when Firebase is done,
            // and AppNav's startDestination logic (or your nav host) handles the
            // transition back to the login screen.
            if (isSigningOut) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .size(24.dp)
                )
            } else {
                CampusLiftButton(
                    text = "Sign Out",
                    onClick = {
                        isSigningOut = true
                        authViewModel.signOut(googleHelper)
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}