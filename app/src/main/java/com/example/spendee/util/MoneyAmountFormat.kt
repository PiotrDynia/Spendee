package com.example.spendee.util

fun isValidNumberInput(input: String): Boolean {
    val regex = "^\\d{0,8}(\\.\\d{0,2})?$".toRegex()
    return input.isBlank() || regex.matches(input)
}