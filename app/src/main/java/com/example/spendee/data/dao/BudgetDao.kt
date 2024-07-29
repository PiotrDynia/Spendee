package com.example.spendee.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.spendee.data.entities.Budget
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budget")
    fun getAllBudgets(): Flow<List<Budget>>

    @Upsert
    suspend fun upsertBudget(budget: Budget)

    @Delete
    suspend fun deleteBudget(budget: Budget)
}