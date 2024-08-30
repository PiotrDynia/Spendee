package com.example.spendee.feature_expenses.presentation.add_edit_expense

sealed class AddEditExpenseEvent {
    data class OnAmountChange(val amount: String) : AddEditExpenseEvent()
    data class OnDescriptionChange(val description: String) : AddEditExpenseEvent()
    data class OnCategoryChange(val categoryId: Int) : AddEditExpenseEvent()
    data object OnSaveExpenseClick : AddEditExpenseEvent()
}
