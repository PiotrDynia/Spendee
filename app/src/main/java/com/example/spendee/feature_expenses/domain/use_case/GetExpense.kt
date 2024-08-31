package com.example.spendee.feature_expenses.domain.use_case

import com.example.spendee.feature_expenses.domain.model.Expense
import com.example.spendee.feature_expenses.domain.repository.ExpenseRepository

class GetExpense(
    private val repository: ExpenseRepository
) {

    suspend operator fun invoke(id: Int) : Expense? {
        return repository.getExpenseById(id)
    }
}