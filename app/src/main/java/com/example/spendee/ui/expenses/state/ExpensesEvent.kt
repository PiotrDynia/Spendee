package com.example.spendee.ui.expenses.state

import com.example.spendee.data.entities.Expense

sealed class ExpensesEvent {
    data object OnAddExpenseClick : ExpensesEvent()
    data class OnExpenseClick(val expense: Expense) : ExpensesEvent()
    data class OnDeleteExpense(val expense: Expense) : ExpensesEvent()
    data object OnUndoDelete : ExpensesEvent()
}