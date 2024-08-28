package com.example.spendee.ui.goals.state

import com.example.spendee.data.entities.Goal

sealed class GoalsEvent {
    data object OnAddGoalClick : GoalsEvent()
    data class OnGoalClick(val goal: Goal) : GoalsEvent()
    data class OnDeleteGoal(val goal: Goal) : GoalsEvent()
    data object OnUndoDeleteGoal : GoalsEvent()
}