package com.example.spendee.ui.budget

sealed class BudgetEvent {
    data object OnSetBudgetClick : BudgetEvent()
    data object OnDeleteBudget : BudgetEvent()
}