package com.example.spendee.feature_budget.presentation.budget

sealed class BudgetEvent {
    data object OnSetBudgetClick : BudgetEvent()
    data object OnDeleteBudget : BudgetEvent()
}