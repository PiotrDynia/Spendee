package com.example.spendee.feature_goals.data.repository

import com.example.spendee.feature_goals.domain.model.Goal
import com.example.spendee.feature_goals.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeGoalsRepository : GoalRepository {
    private val goals = mutableListOf<Goal>()

    override fun getAllGoals(): Flow<List<Goal>> {
        return flow { emit(goals) }
    }

    override suspend fun getGoalById(id: Int): Goal? {
        return goals.find { it.id == id }
    }

    override suspend fun upsertGoal(goal: Goal) {
        val index = goals.indexOfFirst { it.id == goal.id }
        if (index != -1) {
            goals[index] = goal
        } else {
            goals.add(goal)
        }
    }

    override suspend fun deleteGoal(goal: Goal) {
        goals.remove(goal)
    }
}