package com.example.campuslift

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campuslift.Auth.AuthState
import com.example.campuslift.Navigation.AppNav
import com.example.campuslift.ViewModels.AuthViewModel
import com.example.campuslift.ViewModels.UserViewModel
import com.example.campuslift.ui.theme.CampusLiftTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CampusLiftTheme {
                // your existing root composable / navigation host
                // plus the sync observer below
                SyncObserver()
                AppNav()   // ← replace with whatever your nav host is called
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun SyncObserver(
    authVm: AuthViewModel = viewModel(),
    userVm: UserViewModel = viewModel()
) {
    val authState by authVm.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            // Idempotent — the API returns the existing user if already there.
            userVm.syncAfterLogin()
        }
    }
}