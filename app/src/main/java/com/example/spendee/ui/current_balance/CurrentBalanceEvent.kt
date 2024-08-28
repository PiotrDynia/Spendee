package com.example.spendee.ui.current_balance

import com.example.spendee.data.entities.Expense

sealed class CurrentBalanceEvent {
    data object OnSetBalanceClick : CurrentBalanceEvent()
    data object OnShowMoreClick : CurrentBalanceEvent()
    data class OnExpenseClick(val expense: Expense) : CurrentBalanceEvent()
    data object OnCancelSetBalanceClick : CurrentBalanceEvent()
    data object OnConfirmSetBalanceClick : CurrentBalanceEvent()
    data class OnAmountChange(val amount: String) : CurrentBalanceEvent()
}