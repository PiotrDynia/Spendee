package com.example.spendee.feature_expenses.presentation.expenses

import com.example.spendee.feature_expenses.domain.model.Expense

sealed class ExpensesEvent {
    data object OnAddExpenseClick : ExpensesEvent()
    data class OnExpenseClick(val expense: Expense) : ExpensesEvent()
    data class OnDeleteExpense(val expense: Expense) : ExpensesEvent()
    data object OnUndoDelete : ExpensesEvent()
}