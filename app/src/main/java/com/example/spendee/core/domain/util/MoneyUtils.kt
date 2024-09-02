package com.example.spendee.core.domain.util

import java.time.LocalDate
import kotlin.math.round

fun isValidNumberInput(input: String): Boolean {
    val regex = "^\\d{0,8}(\\.\\d{0,2})?$".toRegex()
    return input.isBlank() || regex.matches(input)
}

fun calculateDailySpending(date: LocalDate, totalAmount: Double): Double {
    val daysDiff = getDaysFromNow(date)

    return if (daysDiff > 0) {
        val dailySpending = totalAmount / daysDiff
        round(dailySpending * 100) / 100
    } else {
        round(totalAmount * 100) / 100
    }
}