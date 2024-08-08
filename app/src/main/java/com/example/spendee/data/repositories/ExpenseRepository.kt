package com.example.spendee.data.repositories

import com.example.spendee.data.entities.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun getAllExpenses(): Flow<List<Expense>>
    suspend fun getExpenseById(id: Int): Expense?
    suspend fun upsertExpense(expense: Expense)
    suspend fun deleteExpense(expense: Expense)
}