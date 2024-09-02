package com.example.spendee.feature_expenses.domain.use_case

import com.example.spendee.feature_budget.domain.repository.BudgetRepository
import com.example.spendee.feature_current_balance.domain.repository.BalanceRepository
import com.example.spendee.feature_expenses.domain.model.Expense
import com.example.spendee.feature_expenses.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class DeleteExpense(
    private val expenseRepository: ExpenseRepository,
    private val budgetRepository: BudgetRepository,
    private val balanceRepository: BalanceRepository
) {
    suspend operator fun invoke(expense: Expense) {
        val balance = balanceRepository.getBalance().first()
        val budget = budgetRepository.getBudget().firstOrNull()
        expenseRepository.deleteExpense(expense)
        balance.let {
            val updatedBalance = it.copy(amount = it.amount + expense.amount)
            balanceRepository.upsertBalance(updatedBalance)
        }
        budget?.let {
            val updatedBudget = it.copy(
                leftToSpend = it.leftToSpend + expense.amount,
                totalSpent = (it.totalSpent - expense.amount).coerceAtLeast(0.0)
            )
            budgetRepository.upsertBudget(updatedBudget)
        }
    }
}