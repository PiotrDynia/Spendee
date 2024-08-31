package com.example.spendee.feature_goals.domain.use_case

import com.example.spendee.feature_goals.domain.model.Goal
import com.example.spendee.feature_goals.domain.repository.GoalRepository

class GetGoal(
    private val repository: GoalRepository
) {
    suspend operator fun invoke(id: Int) : Goal? {
        return repository.getGoalById(id)
    }
}