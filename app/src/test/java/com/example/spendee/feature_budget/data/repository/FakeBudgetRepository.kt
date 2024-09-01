package com.example.spendee.feature_budget.data.repository

import com.example.spendee.feature_budget.domain.model.Budget
import com.example.spendee.feature_budget.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate

class FakeBudgetRepository : BudgetRepository {

    private var budget = Budget(
        totalAmount = 0.0,
        leftToSpend = 0.0,
        totalSpent = 0.0,
        startDate = LocalDate.now(),
        endDate = LocalDate.now(),
        isExceeded = false,
        isExceedNotificationEnabled = false
    )

    override fun getBudget(): Flow<Budget> {
        return flow { emit(budget) }
    }

    override suspend fun upsertBudget(budget: Budget) {
        this.budget = budget
    }

    override suspend fun deleteBudget(budget: Budget) {
        this.budget = Budget(
            totalAmount = 0.0,
            leftToSpend = 0.0,
            totalSpent = 0.0,
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            isExceeded = false,
            isExceedNotificationEnabled = false
        )
    }
}