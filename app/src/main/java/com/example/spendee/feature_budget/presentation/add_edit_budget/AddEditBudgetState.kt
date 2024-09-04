package com.example.spendee.feature_budget.presentation.add_edit_budget

data class AddEditBudgetState(
    val amount: String = "",
    val startingDayPicker: Int? = null,
    val startingDayInput: Int? = null,
    val isExceedButtonPressed: Boolean = false
)