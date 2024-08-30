package com.example.spendee.feature_current_balance.presentation.current_balance

import com.example.spendee.feature_current_balance.domain.model.Balance
import com.example.spendee.feature_expenses.domain.model.Expense

data class CurrentBalanceState(
    var currentAmount: String = "",
    var isDialogOpen: Boolean = false,
    var balance: Balance = Balance(id = 1, amount = 0.0),
    var latestExpenses: List<Expense> = emptyList()
)
