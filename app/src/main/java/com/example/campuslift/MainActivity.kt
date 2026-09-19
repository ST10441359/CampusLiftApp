package com.example.campuslift

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campuslift.Auth.AuthState
import com.example.campuslift.Navigation.AppNav
import com.example.campuslift.ViewModels.AuthViewModel
import com.example.campuslift.ViewModels.SettingsViewModel
import com.example.campuslift.ViewModels.UserViewModel
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CampusLiftRoot()
        }
    }
}

/**
 * Root composable.
 * - Applies dark/light theme from user settings
 * - Runs the SyncObserver so users get synced to the API after login
 * - Hosts the AppNav navigation graph
 */
@Composable
fun CampusLiftRoot() {

    // ---- Sync observer (Suvan's API integration) ----
    SyncObserver()

    // ---- Theme switching (Keshvir's settings integration) ----
    val currentUid = rememberAuthUid()
    val settingsViewModel: SettingsViewModel = viewModel(key = "settings_$currentUid")
    val darkModeEnabled by settingsViewModel.darkMode.collectAsStateWithLifecycle()

    val colorScheme = if (darkModeEnabled) {
        darkColorScheme(
            primary = Color(0xFFFF6B35),
            background = Color(0xFF121212),
            surface = Color(0xFF1E1E1E),
            onBackground = Color.White,
            onSurface = Color.White
        )
    } else {
        lightColorScheme(
            primary = Color(0xFFFF6B35),
            background = Color(0xFFF5F5F5),
            surface = Color.White,
            onBackground = Color(0xFF1A237E),
            onSurface = Color(0xFF1A237E)
        )
    }

    MaterialTheme(colorScheme = colorScheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AppNav()
        }
    }
}

/**
 * Watches the Firebase auth state and syncs the user with the API after login.
 * Added by Suvan (original author).
 */
@Composable
private fun SyncObserver(
    authVm: AuthViewModel = viewModel(),
    userVm: UserViewModel = viewModel()
) {
    val authState by authVm.authState.collectAsState()
    var hasSynced = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated && !hasSynced.value) {
            hasSynced.value = true
            kotlinx.coroutines.delay(800)
            userVm.syncAfterLogin()
        }
    }
}

/**
 * Returns the current Firebase UID as a Compose state.
 * Re-emits whenever the user signs in or out.
 * Added by Keshvir (for per-user settings).
 */
@Composable
private fun rememberAuthUid(): String {
    val auth = FirebaseAuth.getInstance()
    val uid = androidx.compose.runtime.remember {
        androidx.compose.runtime.mutableStateOf(auth.currentUser?.uid ?: "guest")
    }

    androidx.compose.runtime.DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            uid.value = firebaseAuth.currentUser?.uid ?: "guest"
        }
        auth.addAuthStateListener(listener)
        onDispose { auth.removeAuthStateListener(listener) }
    }

    return uid.value
}