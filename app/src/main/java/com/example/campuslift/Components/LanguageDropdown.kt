package com.example.campuslift.Components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

/**
 * CampusLift language dropdown.
 * Displays current language and lets the user pick between English and isiZulu.
 *
 * Author: Keshvir Parthab (ST10451537)
 */
@Composable
fun LanguageDropdown(
    currentLanguage: String,   // "en" or "zu"
    onLanguageSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val displayText = when (currentLanguage) {
        "zu" -> "isiZulu"
        else -> "English"
    }

    Box(modifier = modifier) {

        Text(
            text = "$displayText  ▾",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier
                .clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("English") },
                onClick = {
                    onLanguageSelected("en")
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("isiZulu") },
                onClick = {
                    onLanguageSelected("zu")
                    expanded = false
                }
            )
        }
    }
}