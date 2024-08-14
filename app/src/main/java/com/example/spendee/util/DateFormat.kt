package com.example.spendee.util

import java.text.DateFormat.getDateInstance
import java.text.ParseException
import java.util.Date
import java.util.concurrent.TimeUnit

fun dateToString(date: Date): String {
    val dateFormat = getDateInstance()
    return dateFormat.format(date)
}

fun millisToString(millis: Long): String {
    val dateFormat = getDateInstance()
    val date = Date(millis)
    return dateFormat.format(date)
}

fun stringToDate(dateString: String): Date? {
    val dateFormat = getDateInstance()
    return try {
        dateFormat.parse(dateString)
    } catch (e: ParseException) {
        e.printStackTrace()
        null
    }
}

fun differenceInDays(date1: Date, date2: Date): Long {
    val diffInMillis = date2.time - date1.time
    return TimeUnit.MILLISECONDS.toDays(diffInMillis)
}