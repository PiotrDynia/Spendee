package com.example.spendee.data.repositories

import com.example.spendee.data.dao.ExpenseDao
import com.example.spendee.data.entities.Expense
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.math.exp

class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao
) : ExpenseRepository {
    override fun getAllExpenses(): Flow<List<Expense>> {
        return expenseDao.getAllExpenses()
    }

    override suspend fun upsertExpense(expense: Expense) {
        expenseDao.upsertExpense(expense)
    }

    override suspend fun deleteExpense(expense: Expense) {
        expenseDao.deleteExpense(expense)
    }
}