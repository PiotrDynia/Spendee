package com.example.spendee.core.domain.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateFormatTest {

    private lateinit var dateFormatter: DateTimeFormatter

    @Before
    fun setUp() {
        dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    }

    @Test
    fun `Format date to string, get correct string`() {
        val date = LocalDate.of(2024, 9, 12)
        val dateString = dateToString(date)
        val expectedString = "2024-09-12"

        assertEquals(expectedString, dateString)
    }

    @Test
    fun `Format string to date, get correct date`() {
        val stringDate = "2024-09-12"
        val date = stringToDate(stringDate)
        val expectedDate = LocalDate.of(2024, 9, 12)

        assertEquals(expectedDate, date)
    }

    @Test
    fun `Format incorrect string to date, get null`() {
        val incorrectString = "Incorrect string"
        val date = stringToDate(incorrectString)

        assertNull(date)
    }

    @Test
    fun `Get days from now, correctly count days`() {
        val today = LocalDate.now()
        val weekFromNow = today.plusWeeks(1)

        val expectedDifference = 7L
        val actualDifference = getDaysFromNow(weekFromNow)

        assertEquals(expectedDifference, actualDifference)
    }
}