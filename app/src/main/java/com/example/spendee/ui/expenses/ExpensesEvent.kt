package com.example.spendee.ui.expenses

import com.example.spendee.data.entities.Expense

sealed class ExpensesEvent {
    data object OnAddExpenseClick : ExpensesEvent()
    data class OnExpenseClick(val expense: Expense) : ExpensesEvent()
}