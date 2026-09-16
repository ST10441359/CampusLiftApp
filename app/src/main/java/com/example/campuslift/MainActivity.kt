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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campuslift.Navigation.AppNav
import com.example.campuslift.ViewModels.SettingsViewModel
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CampusLiftApp()
        }
    }
}

@Composable
fun CampusLiftApp() {
    // Observe the signed-in user's UID. When this changes, the settings reload.
    val currentUid = rememberAuthUid()

    // Use the UID as a key so a fresh SettingsViewModel is created per user.
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
 * Returns the current Firebase UID as a Compose state.
 * Re-emits whenever the user signs in or out.
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