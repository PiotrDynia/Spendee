package com.example.spendee.data.repositories

import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.spendee.data.entities.Goal
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    fun getAllGoals(): Flow<List<Goal>>
    suspend fun upsertGoal(goal: Goal)
    suspend fun deleteGoal(goal: Goal)
}