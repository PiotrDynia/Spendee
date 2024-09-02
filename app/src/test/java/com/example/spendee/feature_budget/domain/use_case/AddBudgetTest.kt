package com.example.spendee.feature_budget.domain.use_case

import com.example.spendee.R
import com.example.spendee.feature_budget.data.repository.FakeBudgetRepository
import com.example.spendee.feature_budget.domain.model.InvalidBudgetException
import com.example.spendee.feature_budget.presentation.add_edit_budget.AddEditBudgetState
import com.example.spendee.feature_expense.data.repository.FakeExpensesRepository
import com.example.spendee.feature_expenses.domain.model.Expense
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

class AddBudgetTest {
    private lateinit var addBudget: AddBudget
    private lateinit var budgetRepository: FakeBudgetRepository
    private lateinit var expensesRepository: FakeExpensesRepository

    @Before
    fun setUp() {
        budgetRepository = FakeBudgetRepository()
        expensesRepository = FakeExpensesRepository()
        addBudget = AddBudget(budgetRepository, expensesRepository)
    }

    @Test
    fun `Add budget with empty amount, throw an exception`() {
        val budgetWithEmptyAmountState = AddEditBudgetState()
        val exception = assertThrows(InvalidBudgetException::class.java) {
            runBlocking {
                addBudget(budgetWithEmptyAmountState)
            }
        }

        assertEquals(R.string.amount_cant_be_empty, exception.messageResId)
    }

    @Test
    fun `Add budget without starting day, throw an exception`() {
        val budgetWithoutStartingDayState = AddEditBudgetState(amount = "40")
        val exception = assertThrows(InvalidBudgetException::class.java) {
            runBlocking {
                addBudget(budgetWithoutStartingDayState)
            }
        }

        assertEquals(R.string.please_select_a_starting_day, exception.messageResId)
    }

    @Test
    fun `Add budget with starting day in current month, correctly set dates`() = runBlocking {
        val today = LocalDate.now()
        val state = AddEditBudgetState(amount = "100", startingDay = today.dayOfMonth - 1)
        addBudget(state)


        val budget = budgetRepository.getBudget().firstOrNull()

        assertEquals(LocalDate.of(today.year, today.month, today.dayOfMonth - 1), budget?.startDate)
        assertEquals(LocalDate.of(today.year, today.month, today.dayOfMonth - 1).plusMonths(1).minusDays(1), budget?.endDate)
    }

    @Test
    fun `Add budget with starting day in previous month, correctly set dates`() = runBlocking {
        val today = LocalDate.now()
        val state = AddEditBudgetState(amount = "100", startingDay = today.dayOfMonth + 1)
        addBudget(state)

        val previousMonth = YearMonth.of(today.year, today.month).minusMonths(1)
        val budget = budgetRepository.getBudget().firstOrNull()

        assertEquals(LocalDate.of(previousMonth.year, previousMonth.monthValue, today.dayOfMonth + 1), budget?.startDate)
        assertEquals(LocalDate.of(previousMonth.year, previousMonth.monthValue, today.dayOfMonth + 1).plusMonths(1).minusDays(1), budget?.endDate)
    }

    @Test
    fun `Add budget with starting day as the last day of the month, correctly set dates`() = runBlocking {
        val today = LocalDate.now()
        val lastDayOfMonth = YearMonth.of(today.year, today.month).lengthOfMonth()
        val startingMonth = if (lastDayOfMonth > today.dayOfMonth) {
            YearMonth.of(today.year, today.month).minusMonths(1)
        } else {
            YearMonth.of(today.year, today.month)
        }
        val state = AddEditBudgetState(amount = "100", startingDay = lastDayOfMonth)
        addBudget(state)

        val budget = budgetRepository.getBudget().firstOrNull()

        assertEquals(LocalDate.of(startingMonth.year, startingMonth.month, minOf(startingMonth.lengthOfMonth(), lastDayOfMonth)), budget?.startDate)
        assertEquals(LocalDate.of(startingMonth.year, startingMonth.month, minOf(startingMonth.lengthOfMonth(), lastDayOfMonth)).plusMonths(1).minusDays(1), budget?.endDate)
    }

    @Test
    fun `Add budget with expenses already present, correctly set amount spent`() = runBlocking {
        val today = LocalDate.now()
        val startingDay = today.minusDays(5).dayOfMonth

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
        val sumOfExpenses = exampleExpenses.sumOf { it.amount }

        expensesRepository.upsertExpense(exampleExpenses.first())
        expensesRepository.upsertExpense(exampleExpenses.elementAt(1))

        val budgetAmount = "100"
        val state = AddEditBudgetState(amount = budgetAmount, startingDay = startingDay)
        addBudget(state)

        val budget = budgetRepository.getBudget().firstOrNull()

        assertEquals(sumOfExpenses, budget?.totalSpent)
        assertEquals(budgetAmount.toDouble() - sumOfExpenses, budget?.leftToSpend)
    }

    @Test
    fun `Add budget with expenses exceeding the budget, set left to spend to 0`() = runBlocking {
        val today = LocalDate.now()
        val startingDay = today.minusDays(5).dayOfMonth

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
        expensesRepository.upsertExpense(exampleExpenses.first())
        expensesRepository.upsertExpense(exampleExpenses.elementAt(1))

        val budgetAmount = "50"
        val expectedLeftToSpend = 0.0
        val state = AddEditBudgetState(amount = budgetAmount, startingDay = startingDay)
        addBudget(state)

        val budget = budgetRepository.getBudget().firstOrNull()

        assertEquals(expectedLeftToSpend, budget?.leftToSpend)
        assertTrue(budget!!.isExceeded)
    }

    @Test
    fun `Add budget, does not throw`() = runBlocking {
        val today = LocalDate.now()

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
        exampleExpenses.forEach {
            expensesRepository.upsertExpense(it)
        }

        val budgetAmount = "100"
        val state = AddEditBudgetState(amount = budgetAmount, startingDay = today.dayOfMonth)
        try {
            addBudget(state)
        } catch (e: Exception) {
            fail("Expected no exception to be thrown, but got: ${e.message}")
        }
    }
}