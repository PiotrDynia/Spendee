package com.example.spendee.feature_expenses.presentation.add_edit_expense

import com.example.spendee.feature_expenses.domain.model.Expense
import java.time.LocalDate

data class AddEditExpenseState(
    val expense: Expense? = null,
    val amount: String = "",
    val originalAmount: String = "",
    val description: String = "",
    val date: LocalDate? = null,
    val categoryId: Int = 0
)
