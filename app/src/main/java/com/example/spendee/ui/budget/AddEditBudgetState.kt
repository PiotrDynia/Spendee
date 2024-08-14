package com.example.spendee.ui.budget

data class AddEditBudgetState(
    val amount: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val isStartDatePickerOpened: Boolean = false,
    val isEndDatePickerOpened: Boolean = false,
    val isExceedButtonPressed: Boolean = false,
    val isReach80PercentButtonPressed: Boolean = false
)