package com.example.campuslift.Navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.campuslift.Auth.AuthState
import com.example.campuslift.Screens.AddVehicleScreen
import com.example.campuslift.Screens.AlertsScreen
import com.example.campuslift.Screens.BookingDetailsScreen
import com.example.campuslift.Screens.CreateRideScreen
import com.example.campuslift.Screens.HomeScreen
import com.example.campuslift.Screens.LoginScreen
import com.example.campuslift.Screens.MyRidesScreen
import com.example.campuslift.Screens.RegisterScreen
import com.example.campuslift.Screens.RideDetailsScreen
import com.example.campuslift.Screens.SettingsScreen
import com.example.campuslift.ViewModels.AuthViewModel

private val bottomNavRoutes = setOf("home", "myRides", "alerts", "settings")

@Composable
fun AppNav() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    val startDestination = when (authState) {
        is AuthState.Authenticated -> "home"
        else -> "login"
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomNavRoutes) {
                BottomNavBar(navController = navController)
            }
        }
    ) { scaffoldPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = androidx.compose.ui.Modifier.padding(scaffoldPadding)
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
                    onNavigateToAddVehicle = { navController.navigate("addVehicle") },
                    onNavigateToMyRides = { navController.navigate("myRides") },
                    onRideSelected = { navController.navigate("rideDetails") }
                )
            }

            composable("createRide") {
                CreateRideScreen(
                    onBack = { navController.popBackStack() },
                    onRideCreated = { navController.popBackStack() }
                )
            }

            composable("addVehicle") {
                AddVehicleScreen(
                    onBack = { navController.popBackStack() },
                    onContinueToCreateRide = {
                        navController.navigate("createRide") {
                            popUpTo("home")
                        }
                    },
                    onBackToHome = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
            }

            composable("rideDetails") {
                RideDetailsScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable("myRides") {
                MyRidesScreen(
                    onBack = { navController.popBackStack() },
                    onBookingSelected = { navController.navigate("bookingDetails") }
                )
            }

            composable("bookingDetails") {
                BookingDetailsScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable("alerts") {
                AlertsScreen()
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
}