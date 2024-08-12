package com.example.spendee.data.repositories

import com.example.spendee.data.dao.GoalDao
import com.example.spendee.data.entities.Goal
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