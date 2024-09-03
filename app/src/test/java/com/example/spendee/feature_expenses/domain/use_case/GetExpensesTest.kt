package com.example.spendee.feature_expenses.domain.use_case

import com.example.spendee.feature_expenses.data.repository.FakeExpensesRepository
import com.example.spendee.feature_expenses.domain.model.Expense
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class GetExpensesTest {

    private lateinit var getExpenses: GetExpenses
    private lateinit var repository: FakeExpensesRepository

    @Before
    fun setUp() {
        repository = FakeExpensesRepository()
        getExpenses = GetExpenses(repository)
    }

    @Test
    fun `Retrieve all expenses, correctly return`() = runBlocking {
        val exampleExpenses = listOf(
            Expense(
                id = 1,
                amount = 50.0,
                description = "Example expense 1",
                date = LocalDate.now(),
                categoryId = 5
            ),
            Expense(
                id = 2,
                amount = 30.0,
                description = "Example expense 2",
                date = LocalDate.now(),
                categoryId = 3
            )
        )
        runBlocking {
            exampleExpenses.forEach { repository.upsertExpense(it) }
        }

        assertEquals(exampleExpenses, getExpenses().firstOrNull())
    }

    @Test
    fun `Retrieve empty list, correctly return`() = runBlocking {
        assertEquals(emptyList<Expense>(), getExpenses().firstOrNull())
    }
}