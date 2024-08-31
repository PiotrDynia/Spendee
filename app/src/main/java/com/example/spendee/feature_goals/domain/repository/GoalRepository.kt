package com.example.spendee.feature_goals.domain.repository

import com.example.spendee.feature_goals.domain.model.Goal
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    fun getAllGoals(): Flow<List<Goal>>
    suspend fun getGoalById(id: Int) : Goal?
    suspend fun upsertGoal(goal: Goal)
    suspend fun deleteGoal(goal: Goal)
}