package com.example.spendee.util

import java.time.LocalDate
import kotlin.math.round

fun isValidNumberInput(input: String): Boolean {
    val regex = "^\\d{0,8}(\\.\\d{0,2})?$".toRegex()
    return input.isBlank() || regex.matches(input)
}

fun calculateDailySpending(date1: LocalDate, date2: LocalDate, totalAmount: Double): Double? {
    // TODO calculate between end date and now instead of between start and end
    val daysDiff = differenceInDays(date1, date2)

    return if (daysDiff > 0) {
        val dailySpending = totalAmount / daysDiff
        round(dailySpending * 100) / 100
    } else {
        null
    }
}