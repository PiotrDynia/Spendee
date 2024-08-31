package com.example.spendee.feature_expenses.domain.repository

import com.example.spendee.feature_expenses.domain.model.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun getAllExpenses(): Flow<List<Expense>>
    suspend fun getExpenseById(id: Int): Expense?
    suspend fun upsertExpense(expense: Expense)
    suspend fun deleteExpense(expense: Expense)
}