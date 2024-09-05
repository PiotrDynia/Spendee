package com.example.spendee.feature_budget.presentation.add_edit_budget

sealed class AddEditBudgetEvent {
    data class OnAmountChange(val amount: String) : AddEditBudgetEvent()
    data object OnCancelStartingDay : AddEditBudgetEvent()
    data class OnChangeStartingDay(val newDay: Int) : AddEditBudgetEvent()
    data object OnConfirmStartingDay : AddEditBudgetEvent()
    data class OnExceedButtonPress(val isPressed: Boolean) : AddEditBudgetEvent()
    data object OnSaveBudgetClick : AddEditBudgetEvent()
}