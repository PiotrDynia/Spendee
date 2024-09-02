package com.example.spendee.core.domain.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class MoneyUtilsTest {
    @Test
    fun `Set blank input, return true`() {
        assertTrue(isValidNumberInput(""))
    }

    @Test
    fun `Set valid integer, return true`() {
        assertTrue(isValidNumberInput("123"))
    }

    @Test
    fun `Set valid decimal, return true`() {
        assertTrue(isValidNumberInput("123.12"))
    }

    @Test
    fun `Set input to more than 2 digits after decimal, return false`() {
        assertFalse(isValidNumberInput("12345678.123"))
    }

    @Test
    fun `Set input with letters, return false`() {
        assertFalse(isValidNumberInput("12a45"))
    }

    @Test
    fun `Set negative number input, return false`() {
        assertFalse(isValidNumberInput("-12345"))
    }

    @Test
    fun `Calculate daily spending with positive days difference, get correct calculation`() {
        val date = LocalDate.now().plusDays(10)
        val totalAmount = 100.0
        val expectedDailySpending = kotlin.math.round((100.0 / 10) * 100) / 100
        val delta = 0.0

        assertEquals(expectedDailySpending, calculateDailySpending(date, totalAmount), delta)
    }

    @Test
    fun `Calculate daily spending with date as today, return total amount`() {
        val date = LocalDate.now()
        val totalAmount = 100.0
        val expectedDailySpending = 100.0
        val delta = 0.0

        assertEquals(expectedDailySpending, calculateDailySpending(date, totalAmount), delta)
    }
}