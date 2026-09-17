package com.example.campuslift.Navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.campuslift.Auth.AuthState
import com.example.campuslift.Screens.HomeScreen
import com.example.campuslift.Screens.LoginScreen
import com.example.campuslift.Screens.RegisterScreen
import com.example.campuslift.Screens.SettingsScreen
import com.example.campuslift.ViewModels.AuthViewModel
import com.example.campuslift.Screens.CreateRideScreen
import com.example.campuslift.Screens.SearchRideScreen
import com.example.campuslift.Screens.RideDetailsScreen

/**
 * Root navigation graph for CampusLift.
 * Handles transitions between auth screens, home, and settings.
 */
@Composable
fun AppNav() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    // Choose start destination based on auth state
    val startDestination = when (authState) {
        is AuthState.Authenticated -> "home"
        else -> "login"
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("login") {
            LoginScreen(
                onNavigateToRegister = { navController.navigate("register") },
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                onSignOut = {
                    authViewModel.signOut()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToCreateRide = { navController.navigate("createRide") },
                onNavigateToSearchRide = { navController.navigate("searchRide") }
            )
        }

        composable("createRide") {
            CreateRideScreen(
                onBack = { navController.popBackStack() },
                onRideCreated = { navController.popBackStack() }
            )
        }
        composable("searchRide") {
            SearchRideScreen(
                onBack = { navController.popBackStack() },
                onRideSelected = { navController.navigate("rideDetails") }
            )
        }

        composable("rideDetails") {
            RideDetailsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("settings") {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onSignOut = {
                    authViewModel.signOut()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}