package com.example.spendee.feature_expenses.presentation.add_edit_expense

import com.example.spendee.feature_expenses.domain.model.Expense

data class AddEditExpenseState(
    val expense: Expense? = null,
    val amount: String = "",
    val originalAmount: String = "",
    val description: String = "",
    val categoryId: Int = 0
)
