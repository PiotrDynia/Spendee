package com.example.spendee.feature_goals.presentation.goals

import com.example.spendee.feature_goals.domain.model.Goal

sealed class GoalsEvent {
    data object OnAddGoalClick : GoalsEvent()
    data class OnGoalClick(val goal: Goal) : GoalsEvent()
    data class OnDeleteGoal(val goal: Goal) : GoalsEvent()
    data object OnUndoDeleteGoal : GoalsEvent()
}