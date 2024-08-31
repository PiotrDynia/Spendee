package com.example.spendee.feature_goals.domain.use_case

import com.example.spendee.feature_goals.domain.model.Goal
import com.example.spendee.feature_goals.domain.repository.GoalRepository

class DeleteGoal(
    private val repository: GoalRepository
) {
    suspend operator fun invoke(goal: Goal) {
        repository.deleteGoal(goal)
    }
}