package com.example.spendee.feature_expenses.domain.use_case

import com.example.spendee.R
import com.example.spendee.core.domain.NotificationService
import com.example.spendee.feature_budget.domain.model.Budget
import com.example.spendee.feature_budget.domain.repository.BudgetRepository
import com.example.spendee.feature_current_balance.domain.repository.BalanceRepository
import com.example.spendee.feature_expenses.domain.model.Expense
import com.example.spendee.feature_expenses.domain.model.InvalidExpenseException
import com.example.spendee.feature_expenses.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class AddExpense(
    private val expenseRepository: ExpenseRepository,
    private val budgetRepository: BudgetRepository,
    private val balanceRepository: BalanceRepository,
    private val notificationService: NotificationService
) {

    @Throws(InvalidExpenseException::class)
    suspend operator fun invoke(originalAmount: Double, isNewExpense: Boolean, expense: Expense) {
        val amount = expense.amount
        when {
            amount == 0.0 -> {
                throw InvalidExpenseException(R.string.amount_cant_be_empty)
            }
            expense.description.isBlank() -> {
                throw InvalidExpenseException(R.string.description_cant_be_empty)
            }
            isBalanceExceeded(originalAmount, isNewExpense, amount) -> {
                throw InvalidExpenseException(R.string.cant_add_expense_your_balance_is_too_low)
            }
        }
        expenseRepository.upsertExpense(expense)

        if (isNewExpense) {
            updateBalanceForNewExpense(amount)
            updateBudgetForNewExpense(amount)
        } else {
            updateBalanceForExistingExpense(originalAmount = originalAmount, newAmount = amount)
            updateBudgetForExistingExpense(originalAmount = originalAmount, newAmount = amount)
        }
    }

    private suspend fun isBalanceExceeded(originalAmount: Double, isNewExpense: Boolean, amount: Double): Boolean {
        val balanceAmount = balanceRepository.getBalance().first().amount
        return if (isNewExpense) amount > balanceAmount else (amount - originalAmount) > balanceAmount
    }

    private suspend fun updateBalanceForNewExpense(amount: Double) {
        val balance = balanceRepository.getBalance().first()
        balance.let {
            balanceRepository.upsertBalance(it.copy(amount = it.amount - amount))
        }
    }

    private suspend fun updateBalanceForExistingExpense(originalAmount: Double, newAmount: Double) {
        val balance = balanceRepository.getBalance().first()
        balance.let {
            balanceRepository.upsertBalance(
                it.copy(amount = it.amount - (newAmount - originalAmount))
            )
        }
    }

    private suspend fun updateBudgetForNewExpense(amount: Double) {
        val budget = budgetRepository.getBudget().firstOrNull()
        budget?.let {
            updateBudget(it, amount)
        }
    }

    private suspend fun updateBudgetForExistingExpense(originalAmount: Double, newAmount: Double) {
        val budget = budgetRepository.getBudget().firstOrNull()
        budget?.let {
            updateBudget(it, newAmount - originalAmount)
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