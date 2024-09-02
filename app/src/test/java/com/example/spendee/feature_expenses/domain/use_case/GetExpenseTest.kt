package com.example.spendee.feature_expenses.domain.use_case

import com.example.spendee.feature_expenses.data.repository.FakeExpensesRepository
import com.example.spendee.feature_expenses.domain.model.Expense
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class GetExpenseTest {

    private lateinit var getExpense: GetExpense
    private lateinit var repository: FakeExpensesRepository

    @Before
    fun setUp() {
        repository = FakeExpensesRepository()
        getExpense = GetExpense(repository)
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
    }

    @Test
    fun `Retrieve an existing expense, correctly return`() = runBlocking {
        val expectedExpense = Expense(
            id = 2,
            amount = 30.0,
            description = "Example expense 2",
            date = LocalDate.now(),
            categoryId = 3
        )
        val retrievedExpense = getExpense(2)
        assertEquals(expectedExpense, retrievedExpense)
    }

    @Test
    fun `Retrieve a non existing expense, return null`() = runBlocking {
        val retrievedExpense = getExpense(4)
        assertEquals(null, retrievedExpense)
    }
}