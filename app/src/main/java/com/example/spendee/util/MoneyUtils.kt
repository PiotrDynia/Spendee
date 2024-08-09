package com.example.spendee.util

import java.util.Date
import kotlin.math.round

fun isValidNumberInput(input: String): Boolean {
    val regex = "^\\d{0,8}(\\.\\d{0,2})?$".toRegex()
    return input.isBlank() || regex.matches(input)
}

fun calculateDailySpending(date1: Date, date2: Date, totalAmount: Double): Double? {
    val daysDiff = differenceInDays(date1, date2)

    return if (daysDiff > 0) {
        val dailySpending = totalAmount / daysDiff
        round(dailySpending * 100) / 100
    } else {
        null
    }
}