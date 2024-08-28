package com.example.spendee.ui.budget.state

data class AddEditBudgetState(
    val amount: String = "",
    val startingDay: Int? = null,
    val isExceedButtonPressed: Boolean = false
)