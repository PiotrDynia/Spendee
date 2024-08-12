package com.example.spendee.data.repositories

import com.example.spendee.data.dao.BudgetDao
import com.example.spendee.data.entities.Budget
import kotlinx.coroutines.flow.Flow

class BudgetRepositoryImpl(
    private val budgetDao: BudgetDao
) : BudgetRepository {
    override fun getBudget(): Flow<Budget> {
        return budgetDao.getBudget()
    }

    override suspend fun updateBudget(amount: Double) {
        return budgetDao.updateBudget(amount)
    }

    override suspend fun upsertBudget(budget: Budget) {
        budgetDao.upsertBudget(budget)
    }

    override suspend fun deleteBudget(budget: Budget) {
        budgetDao.deleteBudget(budget)
    }

}