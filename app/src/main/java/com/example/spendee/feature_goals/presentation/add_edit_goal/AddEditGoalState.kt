package com.example.spendee.feature_goals.presentation.add_edit_goal

import com.example.spendee.feature_goals.domain.model.Goal

data class AddEditGoalState(
    val goal: Goal? = null,
    val description: String = "",
    val targetAmount: String = "",
    val deadline: String = "",
    val isDeadlineDatePickerOpened: Boolean = false,
    val isReachedButtonPressed: Boolean = false
)
