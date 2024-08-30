package com.example.spendee.feature_budget.domain.repository

import com.example.spendee.feature_budget.domain.model.Budget
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun getBudget(): Flow<Budget>
    suspend fun upsertBudget(budget: Budget)
    suspend fun deleteBudget(budget: Budget)
}