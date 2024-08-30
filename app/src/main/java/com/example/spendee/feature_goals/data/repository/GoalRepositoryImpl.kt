package com.example.spendee.feature_goals.data.repository

import com.example.spendee.feature_goals.data.data_source.GoalDao
import com.example.spendee.feature_goals.domain.model.Goal
import com.example.spendee.feature_goals.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow

class GoalRepositoryImpl(
    private val goalDao: GoalDao
) : GoalRepository {
    override fun getAllGoals(): Flow<List<Goal>> {
        return goalDao.getAllGoals()
    }

    override suspend fun getGoalById(id: Int): Goal? {
        return goalDao.getGoalById(id)
    }

    override suspend fun upsertGoal(goal: Goal) {
        goalDao.upsertGoal(goal)
    }

    override suspend fun deleteGoal(goal: Goal) {
        goalDao.deleteGoal(goal)
    }

}