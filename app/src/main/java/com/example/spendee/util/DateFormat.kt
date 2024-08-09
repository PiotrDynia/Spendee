package com.example.spendee.util

import java.text.DateFormat
import java.text.DateFormat.getDateInstance
import java.text.ParseException
import java.util.Date
import java.util.concurrent.TimeUnit

fun dateToString(date: Date): String {
    val dateFormat = getDateInstance()
    return dateFormat.format(date)
}

fun millisToString(millis: Long): String {
    val dateFormat: DateFormat = getDateInstance()
    val date = Date(millis)
    return dateFormat.format(date)
}

fun stringToDate(dateString: String): Date? {
    val dateFormat: DateFormat = getDateInstance()
    return try {
        dateFormat.parse(dateString)
    } catch (e: ParseException) {
        e.printStackTrace()
        null
    }
}

fun differenceInDays(dateString1: String, dateString2: String): Long? {
    val date1 = stringToDate(dateString1)
    val date2 = stringToDate(dateString2)

    return if (date1 != null && date2 != null) {
        val diffInMillis = date2.time - date1.time
        TimeUnit.MILLISECONDS.toDays(diffInMillis)
    } else {
        null
    }
}