package com.example.spendee.ui.goals.state

import com.example.spendee.data.entities.Goal

data class AddEditGoalState(
    val goal: Goal? = null,
    val description: String = "",
    val targetAmount: String = "",
    val deadline: String = "",
    val isDeadlineDatePickerOpened: Boolean = false,
    val isReachedButtonPressed: Boolean = false
)
