package com.example.spendee.ui.budget

data class AddEditBudgetState(
    val amount: String = "",
    val startingDay: Int? = null,
    val isExceedButtonPressed: Boolean = false
)