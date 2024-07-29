package com.example.spendee.data.repositories

import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.spendee.data.entities.Budget
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun getAllBudgets(): Flow<List<Budget>>
    suspend fun upsertBudget(budget: Budget)
    suspend fun deleteBudget(budget: Budget)
}