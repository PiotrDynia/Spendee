package com.example.spendee.feature_expenses.data.repository

import com.example.spendee.feature_expenses.domain.model.Expense
import com.example.spendee.feature_expenses.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeExpensesRepository : ExpenseRepository {

    private val expenses = mutableListOf<Expense>()

    override fun getAllExpenses(): Flow<List<Expense>> {
        return flow { emit(expenses) }
    }

    override suspend fun getExpenseById(id: Int): Expense? {
        return expenses.find { it.id == id }
    }

    override suspend fun upsertExpense(expense: Expense) {
        val index = expenses.indexOfFirst { it.id == expense.id }
        if (index != -1) {
            expenses[index] = expense
        } else {
            expenses.add(expense)
        }
    }

    override suspend fun deleteExpense(expense: Expense) {
        expenses.remove(expense)
    }
}