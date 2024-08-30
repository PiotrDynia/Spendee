package com.example.spendee.feature_budget.domain.use_case

import com.example.spendee.feature_budget.domain.model.Budget
import com.example.spendee.feature_budget.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow

class GetBudget(
    private val repository: BudgetRepository
) {

    operator fun invoke() : Flow<Budget> {
        return repository.getBudget()
    }
}