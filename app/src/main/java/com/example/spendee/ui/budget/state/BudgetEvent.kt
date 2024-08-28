package com.example.spendee.ui.budget.state

sealed class BudgetEvent {
    data object OnSetBudgetClick : BudgetEvent()
    data object OnDeleteBudget : BudgetEvent()
}