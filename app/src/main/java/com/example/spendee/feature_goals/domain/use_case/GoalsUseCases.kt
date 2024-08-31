package com.example.spendee.feature_goals.domain.use_case

data class GoalsUseCases(
    val getGoals: GetGoals,
    val deleteGoal: DeleteGoal,
    val addGoal: AddGoal,
    val getGoal: GetGoal
)
