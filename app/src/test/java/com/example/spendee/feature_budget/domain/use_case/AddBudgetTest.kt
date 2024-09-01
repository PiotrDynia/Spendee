package com.example.spendee.feature_budget.domain.use_case

import com.example.spendee.R
import com.example.spendee.feature_budget.data.repository.FakeBudgetRepository
import com.example.spendee.feature_budget.domain.model.InvalidBudgetException
import com.example.spendee.feature_budget.presentation.add_edit_budget.AddEditBudgetState
import com.example.spendee.feature_expense.data.repository.FakeExpensesRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

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
}