package com.example.spendee.feature_budget.domain.use_case

data class BudgetUseCases(
    val addBudget: AddBudget,
    val deleteBudget: DeleteBudget,
    val getBudget: GetBudget
)
