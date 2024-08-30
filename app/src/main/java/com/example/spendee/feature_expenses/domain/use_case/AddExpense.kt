package com.example.spendee.feature_expenses.domain.use_case

import com.example.spendee.R
import com.example.spendee.core.domain.util.NotificationService
import com.example.spendee.feature_budget.domain.model.Budget
import com.example.spendee.feature_budget.domain.repository.BudgetRepository
import com.example.spendee.feature_current_balance.domain.repository.BalanceRepository
import com.example.spendee.feature_expenses.domain.model.Expense
import com.example.spendee.feature_expenses.domain.model.InvalidExpenseException
import com.example.spendee.feature_expenses.domain.repository.ExpenseRepository
import com.example.spendee.feature_expenses.presentation.add_edit_expense.AddEditExpenseState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class AddExpense(
    private val expenseRepository: ExpenseRepository,
    private val budgetRepository: BudgetRepository,
    private val balanceRepository: BalanceRepository,
    private val notificationService: NotificationService
) {
    // TODO figure out how to reuse this, isNewExpense can be set to true, what's with original amount?
    suspend operator fun invoke(state: AddEditExpenseState, isNewExpense: Boolean, expense: Expense) {
        val amount = state.amount
        when {
            amount.isBlank() -> {
                throw InvalidExpenseException(R.string.amount_cant_be_empty)
            }
            state.description.isBlank() -> {
                throw InvalidExpenseException(R.string.description_cant_be_empty)
            }
            isBalanceExceeded(state.originalAmount, isNewExpense, amount.toDouble()) -> {
                throw InvalidExpenseException(R.string.cant_add_expense_your_balance_is_too_low)
            }
        }
        expenseRepository.upsertExpense(expense)

        if (isNewExpense) {
            updateBalanceForNewExpense(amount.toDouble())
            updateBudgetForNewExpense(amount.toDouble())
        } else {
            updateBalanceForExistingExpense(originalAmount = state.originalAmount, newAmount = amount.toDouble())
            updateBudgetForExistingExpense(originalAmount = state.originalAmount, newAmount = amount.toDouble())
        }
    }

    private suspend fun isBalanceExceeded(originalAmountStr: String, isNewExpense: Boolean, amount: Double): Boolean {
        val balanceAmount = balanceRepository.getBalance().first().amount
        val originalAmount = originalAmountStr.toDoubleOrNull() ?: 0.0
        return if (isNewExpense) amount > balanceAmount else (amount - originalAmount) > balanceAmount
    }

    private suspend fun updateBalanceForNewExpense(amount: Double) {
        val balance = balanceRepository.getBalance().first()
        balance.let {
            balanceRepository.upsertBalance(it.copy(amount = it.amount - amount))
        }
    }

    private suspend fun updateBalanceForExistingExpense(originalAmount: String, newAmount: Double) {
        val balance = balanceRepository.getBalance().first()
        balance.let {
            balanceRepository.upsertBalance(
                it.copy(amount = it.amount - (newAmount - originalAmount.toDouble()))
            )
        }
    }

    private suspend fun updateBudgetForNewExpense(amount: Double) {
        val budget = budgetRepository.getBudget().firstOrNull()
        budget?.let {
            updateBudget(it, amount)
        }
    }

    private suspend fun updateBudgetForExistingExpense(originalAmount: String, newAmount: Double) {
        val budget = budgetRepository.getBudget().firstOrNull()
        budget?.let {
            updateBudget(it, newAmount - originalAmount.toDouble())
        }
    }

    private suspend fun updateBudget(budget: Budget, amountDifference: Double) {
        if (budget.leftToSpend < amountDifference) {
            budget.isExceeded = true
            budget.leftToSpend = 0.0
            if (budget.isExceedNotificationEnabled) {
                notificationService.showBudgetExceededNotification()
            }
        } else {
            budget.isExceeded = false
            budget.leftToSpend -= amountDifference
        }
        budget.totalSpent += amountDifference
        budgetRepository.upsertBudget(budget)
    }
}