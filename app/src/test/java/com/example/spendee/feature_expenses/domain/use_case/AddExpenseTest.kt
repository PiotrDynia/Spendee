package com.example.spendee.feature_expenses.domain.use_case

import com.example.spendee.R
import com.example.spendee.core.domain.util.FakeNotificationService
import com.example.spendee.feature_budget.data.repository.FakeBudgetRepository
import com.example.spendee.feature_current_balance.data.repository.FakeBalanceRepository
import com.example.spendee.feature_current_balance.domain.model.Balance
import com.example.spendee.feature_expenses.data.repository.FakeExpensesRepository
import com.example.spendee.feature_expenses.domain.model.Expense
import com.example.spendee.feature_expenses.domain.model.InvalidExpenseException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class AddExpenseTest {

    private lateinit var addExpense: AddExpense
    private lateinit var expensesRepository: FakeExpensesRepository
    private lateinit var balanceRepository: FakeBalanceRepository
    private lateinit var budgetRepository: FakeBudgetRepository
    private lateinit var notificationService: FakeNotificationService

    @Before
    fun setUp() {
        expensesRepository = FakeExpensesRepository()
        balanceRepository = FakeBalanceRepository()
        budgetRepository = FakeBudgetRepository()
        notificationService = FakeNotificationService()
        addExpense =
            AddExpense(expensesRepository, budgetRepository, balanceRepository, notificationService)
    }

    @Test
    fun `Add expense with empty amount, throw exception`() {
        val expense = assertThrows(InvalidExpenseException::class.java) {
            runBlocking {
                addExpense(
                    originalAmount = 0.0, isNewExpense = true, expense = Expense(
                        id = 1,
                        amount = 0.0,
                        description = "Example expense",
                        date = LocalDate.now(),
                        categoryId = 5
                    )
                )
            }
        }
        assertEquals(R.string.amount_cant_be_empty, expense.messageResId)
    }

    @Test
    fun `Add expense with empty description, throw exception`() {
        val expense = assertThrows(InvalidExpenseException::class.java) {
            runBlocking {
                addExpense(
                    originalAmount = 30.0, isNewExpense = true, expense = Expense(
                        id = 1,
                        amount = 30.0,
                        description = "",
                        date = LocalDate.now(),
                        categoryId = 5
                    )
                )
            }
        }
        assertEquals(R.string.description_cant_be_empty, expense.messageResId)
    }

    @Test
    fun `Add expense that exceeds the budget, throw exception`() = runBlocking {
        balanceRepository.upsertBalance(
            Balance(amount = 20.0)
        )
        val expense = assertThrows(InvalidExpenseException::class.java) {
            runBlocking {
                addExpense(
                    originalAmount = 30.0, isNewExpense = true, expense = Expense(
                        id = 1,
                        amount = 30.0,
                        description = "Example description",
                        date = LocalDate.now(),
                        categoryId = 5
                    )
                )
            }
        }
        assertEquals(R.string.cant_add_expense_your_balance_is_too_low, expense.messageResId)
    }

    @Test
    fun `Edit existing expense that exceeds the budget, throw exception`() = runBlocking {
        balanceRepository.upsertBalance(
            Balance(amount = 20.0)
        )
        val expense = assertThrows(InvalidExpenseException::class.java) {
            runBlocking {
                addExpense(
                    originalAmount = 10.0, isNewExpense = true, expense = Expense(
                        id = 1,
                        amount = 10.0,
                        description = "Example description",
                        date = LocalDate.now(),
                        categoryId = 5
                    )
                )
                addExpense(
                    originalAmount = 10.0, isNewExpense = false, expense = Expense(
                        id = 1,
                        amount = 30.0,
                        description = "Example description",
                        date = LocalDate.now(),
                        categoryId = 5
                    )
                )
            }
        }
        assertEquals(R.string.cant_add_expense_your_balance_is_too_low, expense.messageResId)
    }

    @Test
    fun `Properly add expense, doesn't throw`() = runBlocking {
        balanceRepository.upsertBalance(
            Balance(amount = 50.0)
        )
        try {
            addExpense(
                originalAmount = 30.0, isNewExpense = true, expense = Expense(
                    id = 1,
                    amount = 30.0,
                    description = "Example expense",
                    date = LocalDate.now(),
                    categoryId = 5
                )
            )
        } catch (e: Exception) {
            fail("Excepted no exception but got ${e.message}")
        }
    }
}