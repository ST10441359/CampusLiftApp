package com.example.campuslift.Components

/**
 * CampusLift reusable validation helpers.
 * Returns null if the input is valid, or an error message String if invalid.
 *
 * Author: Keshvir Parthab (ST10451537)
 */
object ValidationUtils {

    /**
     * Validates a South African phone number.
     * Allows digits, spaces, dashes, and a leading +.
     * Must contain at least 9 digits.
     */
    fun validatePhoneNumber(input: String): String? {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return "Phone number cannot be empty"

        val allowed = Regex("^[+0-9\\-\\s]+$")
        if (!allowed.matches(trimmed)) {
            return "Only digits, spaces, dashes and + are allowed"
        }

        val digitCount = trimmed.count { it.isDigit() }
        if (digitCount < 9) return "Phone number is too short"

        return null
    }

    /**
     * Validates an email address (basic check).
     */
    fun validateEmail(input: String): String? {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return "Email cannot be empty"

        val emailPattern = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        if (!emailPattern.matches(trimmed)) return "Please enter a valid email"

        return null
    }

    /**
     * Validates that a required text field isn't blank.
     */
    fun validateRequired(input: String, fieldName: String = "This field"): String? {
        if (input.trim().isEmpty()) return "$fieldName cannot be empty"
        return null
    }

    /**
     * Validates a maximum character length.
     */
    fun validateMaxLength(input: String, max: Int, fieldName: String = "This field"): String? {
        if (input.length > max) return "$fieldName must be under $max characters"
        return null
    }
}