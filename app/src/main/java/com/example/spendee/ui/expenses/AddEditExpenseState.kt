package com.example.spendee.ui.expenses

import com.example.spendee.data.entities.Expense

data class AddEditExpenseState(
    val expense: Expense? = null,
    val amount: String = "",
    val originalAmount: String = "",
    val description: String = "",
    val categoryId: Int = 0
)
