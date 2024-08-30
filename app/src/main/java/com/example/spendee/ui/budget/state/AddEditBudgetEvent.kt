package com.example.spendee.ui.budget.state

sealed class AddEditBudgetEvent {
    data class OnAmountChange(val amount: String) : AddEditBudgetEvent()
    data object OnCancelStartingDay : AddEditBudgetEvent()
    data class OnChangeStartingDay(val newDay: Int) : AddEditBudgetEvent()
    data class OnExceedButtonPress(val isPressed: Boolean) : AddEditBudgetEvent()
    data object OnSaveBudgetClick : AddEditBudgetEvent()
}