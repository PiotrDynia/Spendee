package com.example.spendee.ui.budget

import com.example.spendee.data.entities.Budget

data class AddEditBudgetState(
    val budget: Budget? = null,
    val amount: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val isStartDatePickerOpened: Boolean = false,
    val isEndDatePickerOpened: Boolean = false,
    val isExceedButtonPressed: Boolean = false,
    val isReach80PercentButtonPressed: Boolean = false
)