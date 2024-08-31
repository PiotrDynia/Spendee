package com.example.spendee.feature_budget.domain.util

sealed class BudgetInfoCardType {
    data object SpentCard : BudgetInfoCardType()
    data object YouCanSpendCard : BudgetInfoCardType()
}