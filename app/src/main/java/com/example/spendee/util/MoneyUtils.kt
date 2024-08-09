package com.example.spendee.util

import kotlin.math.round

fun isValidNumberInput(input: String): Boolean {
    val regex = "^\\d{0,8}(\\.\\d{0,2})?$".toRegex()
    return input.isBlank() || regex.matches(input)
}

fun calculateDailySpending(dateString1: String, dateString2: String, totalAmount: Double): Double? {
    val daysDiff = differenceInDays(dateString1, dateString2)

    return if (daysDiff != null && daysDiff > 0) {
        val dailySpending = totalAmount / daysDiff
        round(dailySpending * 100) / 100
    } else {
        null
    }
}