package com.example.spendee.ui.current_balance

import com.example.spendee.data.entities.Balance
import com.example.spendee.data.entities.Expense

data class CurrentBalanceState(
    var currentAmount: Double,
    var originalAmount: Double,
    var isDialogOpen: Boolean,
    var balance: Balance,
    var latestExpenses: List<Expense>
)
