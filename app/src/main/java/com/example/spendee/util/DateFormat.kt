package com.example.spendee.util

import java.text.DateFormat.getDateInstance
import java.text.SimpleDateFormat
import java.util.Date

fun formatDate(date: Date): String {
    val dateFormat = getDateInstance()
    return dateFormat.format(date)
}