package com.example.spendee.feature_current_balance.presentation.current_balance

import com.example.spendee.feature_expenses.domain.model.Expense

sealed class CurrentBalanceEvent {
    data object OnSetBalanceClick : CurrentBalanceEvent()
    data object OnShowMoreClick : CurrentBalanceEvent()
    data class OnExpenseClick(val expense: Expense) : CurrentBalanceEvent()
    data object OnCancelSetBalanceClick : CurrentBalanceEvent()
    data object OnConfirmSetBalanceClick : CurrentBalanceEvent()
    data class OnAmountChange(val amount: String) : CurrentBalanceEvent()
}