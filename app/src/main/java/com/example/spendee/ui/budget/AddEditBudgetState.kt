package com.example.spendee.ui.budget

data class AddEditBudgetState(
    val amount: String = "",
    val startingDay: Int? = null,
    val isExceedButtonPressed: Boolean = false,
    val isReach80PercentButtonPressed: Boolean = false
)