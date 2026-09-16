package com.example.campuslift

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.campuslift.Navigation.AppNav
import com.example.campuslift.ui.theme.CampusLiftTheme

class MainActivity : ComponentActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: launching CampusLift")
        enableEdgeToEdge()
        setContent {
            CampusLiftTheme {
                AppNav()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: activity visible")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: activity hidden")
    }
}