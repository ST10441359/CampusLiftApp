package com.example.campuslift.Components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * CampusLift reusable text input.
 * Shows an error message underneath if [errorMessage] is not null.
 *
 * Author: Keshvir Parthab (ST10451537)
 */
@Composable
fun CampusLiftTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    errorMessage: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = errorMessage != null,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    )

    if (errorMessage != null) {
        Text(
            text = errorMessage,
            color = Color(0xFFD32F2F), // error red
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}