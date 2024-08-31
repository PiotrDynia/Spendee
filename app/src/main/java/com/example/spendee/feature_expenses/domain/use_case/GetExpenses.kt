package com.example.spendee.feature_expenses.domain.use_case

import com.example.spendee.feature_expenses.domain.model.Expense
import com.example.spendee.feature_expenses.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow

class GetExpenses(
    private val repository: ExpenseRepository
) {
    operator fun invoke() : Flow<List<Expense>> {
        return repository.getAllExpenses()
    }
}