package com.example.spendee.ui.budget

import com.example.spendee.data.entities.Budget

data class BudgetState(
    val budget: Budget? = null,
    val totalAmount: String = "",
    val currentAmount: String = "",
    val startDate: String = "",
    val endDate: String = "",
)
