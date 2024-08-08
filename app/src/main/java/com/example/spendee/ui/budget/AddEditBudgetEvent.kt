package com.example.spendee.ui.budget

sealed class AddEditBudgetEvent {
    data class OnAmountChange(val amount: String) : AddEditBudgetEvent()
    data class OnStartDateChange(val startDate: String) : AddEditBudgetEvent()
    data class OnEndDateChange(val endDate: String) : AddEditBudgetEvent()
    data object OnExceedButtonPress : AddEditBudgetEvent()
    data object OnReach80PercentButtonPress : AddEditBudgetEvent()
    data object OnSaveBudgetClick : AddEditBudgetEvent()
}