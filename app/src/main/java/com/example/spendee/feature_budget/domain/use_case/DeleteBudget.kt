package com.example.spendee.feature_budget.domain.use_case

import com.example.spendee.feature_budget.domain.model.Budget
import com.example.spendee.feature_budget.domain.repository.BudgetRepository

class DeleteBudget(
    private val repository: BudgetRepository
) {

    suspend operator fun invoke(budget: Budget) {
        repository.deleteBudget(budget)
    }
}