package com.example.spendee.feature_expenses.domain.use_case

data class ExpensesUseCases(
    val getExpenses: GetExpenses,
    val deleteExpense: DeleteExpense,
    val getExpense: GetExpense,
    val addExpense: AddExpense
)
