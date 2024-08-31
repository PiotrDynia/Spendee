package com.example.spendee.feature_goals.domain.use_case

import com.example.spendee.feature_goals.domain.model.Goal
import com.example.spendee.feature_goals.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow

class GetGoals(
    private val repository: GoalRepository
) {
    operator fun invoke() : Flow<List<Goal>> {
        return repository.getAllGoals()
    }
}