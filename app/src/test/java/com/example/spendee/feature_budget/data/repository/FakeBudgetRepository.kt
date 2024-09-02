package com.example.spendee.feature_budget.data.repository

import com.example.spendee.feature_budget.domain.model.Budget
import com.example.spendee.feature_budget.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow

class FakeBudgetRepository : BudgetRepository {

    private var budget: Budget? = null

    override fun getBudget(): Flow<Budget> {
        return if (budget != null) {
            flow { emit(budget!!) }
        } else {
            emptyFlow()
        }
    }

    override suspend fun upsertBudget(budget: Budget) {
        this.budget = budget
    }

    override suspend fun deleteBudget(budget: Budget) {
        this.budget = null
    }
}