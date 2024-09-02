package com.example.spendee.feature_expenses.domain.use_case

import com.example.spendee.feature_budget.data.repository.FakeBudgetRepository
import com.example.spendee.feature_budget.domain.model.Budget
import com.example.spendee.feature_current_balance.data.repository.FakeBalanceRepository
import com.example.spendee.feature_current_balance.domain.model.Balance
import com.example.spendee.feature_expenses.data.repository.FakeExpensesRepository
import com.example.spendee.feature_expenses.domain.model.Expense
import com.example.spendee.feature_goals.domain.model.Goal
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class DeleteExpenseTest {

    private lateinit var deleteExpense: DeleteExpense
    private lateinit var expenseRepository: FakeExpensesRepository
    private lateinit var budgetRepository: FakeBudgetRepository
    private lateinit var balanceRepository: FakeBalanceRepository

    @Before
    fun setUp() {
        expenseRepository = FakeExpensesRepository()
        budgetRepository = FakeBudgetRepository()
        balanceRepository = FakeBalanceRepository()
        deleteExpense = DeleteExpense(expenseRepository, budgetRepository, balanceRepository)
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
            exampleExpenses.forEach { expenseRepository.upsertExpense(it) }
        }
    }

    @Test
    fun `Delete expense, correctly change list size`() = runBlocking {
        val startingListSize = expenseRepository.getAllExpenses().firstOrNull()?.size ?: 0
        val expenseToDelete = expenseRepository.getExpenseById(2)
        deleteExpense(expenseToDelete!!)
        val expectedListSize = expenseRepository.getAllExpenses().firstOrNull()?.size ?: 0

        assertEquals(startingListSize - 1, expectedListSize)
    }

    @Test
    fun `Delete all expenses, list is empty`() = runBlocking {
        val allGoals = expenseRepository.getAllExpenses().firstOrNull().orEmpty().toList()
        allGoals.forEach {
            deleteExpense(it)
        }

        assertEquals(emptyList<Goal>(), expenseRepository.getAllExpenses().firstOrNull())
    }

    @Test
    fun `Delete expense, balance gets updated`() = runBlocking {
        val balance = Balance(amount = 50.0)
        balanceRepository.upsertBalance(balance)

        val expenseToDelete = expenseRepository.getExpenseById(2)
        deleteExpense(expenseToDelete!!)

        val updatedBalance = balanceRepository.getBalance().first()
        val expectedAmount = 80.0
        val delta = 0.0

        assertEquals(updatedBalance.amount, expectedAmount, delta)
    }

    @Test
    fun `Delete expense, budget gets updated`() = runBlocking {
        val budget = Budget(
            totalAmount = 100.0,
            leftToSpend = 50.0,
            totalSpent = 50.0,
            startDate = LocalDate.now().minusDays(3),
            endDate = LocalDate.now().plusDays(3),
            isExceeded = true,
            isExceedNotificationEnabled = true
        )
        budgetRepository.upsertBudget(budget)

        val expenseToDelete = expenseRepository.getExpenseById(2)
        deleteExpense(expenseToDelete!!)

        val updatedBudget = budgetRepository.getBudget().first()
        val expectedLeftToSpend = 80.0
        val expectedTotalSpent = 20.0
        val delta = 0.0

        assertEquals(updatedBudget.leftToSpend, expectedLeftToSpend, delta)
        assertEquals(updatedBudget.totalSpent, expectedTotalSpent, delta)
    }

    @Test
    fun `Delete expense, total spent in budget gets updated to 0`() = runBlocking {
        val budget = Budget(
            totalAmount = 100.0,
            leftToSpend = 80.0,
            totalSpent = 20.0,
            startDate = LocalDate.now().minusDays(3),
            endDate = LocalDate.now().plusDays(3),
            isExceeded = true,
            isExceedNotificationEnabled = true
        )
        budgetRepository.upsertBudget(budget)

        val expenseToDelete = expenseRepository.getExpenseById(2)
        deleteExpense(expenseToDelete!!)

        val updatedBudget = budgetRepository.getBudget().first()
        val expectedTotalSpent = 0.0
        val delta = 0.0

        assertEquals(updatedBudget.totalSpent, expectedTotalSpent, delta)
    }
}