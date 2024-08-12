package com.example.spendee.data.repositories

import com.example.spendee.data.entities.Budget
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun getBudget(): Flow<Budget>
    suspend fun updateBudget(amount: Double)
    suspend fun upsertBudget(budget: Budget)
    suspend fun deleteBudget(budget: Budget)
}