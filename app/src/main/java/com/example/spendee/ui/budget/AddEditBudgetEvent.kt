package com.example.spendee.ui.budget

sealed class AddEditBudgetEvent {
    data class OnAmountChange(val amount: String) : AddEditBudgetEvent()
    data class OnStartDateChange(val startDate: String) : AddEditBudgetEvent()
    data class OnEndDateChange(val endDate: String) : AddEditBudgetEvent()
    data object OnOpenStartDatePicker : AddEditBudgetEvent()
    data object OnCloseStartDatePicker : AddEditBudgetEvent()
    data object OnOpenEndDatePicker : AddEditBudgetEvent()
    data object OnCloseEndDatePicker : AddEditBudgetEvent()
    data class OnExceedButtonPress(val isPressed: Boolean) : AddEditBudgetEvent()
    data class OnReach80PercentButtonPress(val isPressed: Boolean) : AddEditBudgetEvent()
    data object OnSaveBudgetClick : AddEditBudgetEvent()
}