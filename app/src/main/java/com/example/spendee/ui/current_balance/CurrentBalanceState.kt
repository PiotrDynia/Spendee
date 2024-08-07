package com.example.spendee.ui.current_balance

import com.example.spendee.data.entities.Balance
import com.example.spendee.data.entities.Expense

data class CurrentBalanceState(
    var currentAmount: String = "",
    var originalAmount: String = "",
    var isDialogOpen: Boolean = false,
    var balance: Balance = Balance(id = 1, amount = 0.0),
    var latestExpenses: List<Expense> = emptyList()
)
