package com.example.spendee.data.repositories

import com.example.spendee.data.entities.Goal
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    fun getAllGoals(): Flow<List<Goal>>
    suspend fun getGoalById(id: Int) : Goal?
    suspend fun upsertGoal(goal: Goal)
    suspend fun deleteGoal(goal: Goal)
}