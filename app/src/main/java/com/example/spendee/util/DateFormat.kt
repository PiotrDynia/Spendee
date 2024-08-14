package com.example.spendee.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit

private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

fun dateToString(date: LocalDate): String {
    return dateFormatter.format(date)
}

fun millisToString(millis: Long): String {
    val date = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
    return dateToString(date)
}

fun stringToDate(dateString: String): LocalDate? {
    return try {
        LocalDate.parse(dateString, dateFormatter)
    } catch (e: DateTimeParseException) {
        e.printStackTrace()
        null
    }
}

fun differenceInDays(date1: LocalDate, date2: LocalDate): Long {
    return ChronoUnit.DAYS.between(date1, date2)
}