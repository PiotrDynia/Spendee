package com.example.spendee.data.repositories

import com.example.spendee.data.dao.BudgetDao
import com.example.spendee.data.entities.Budget
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BudgetRepositoryImpl @Inject constructor(
    private val budgetDao: BudgetDao
) : BudgetRepository {
    override fun getAllBudgets(): Flow<List<Budget>> {
        return budgetDao.getAllBudgets()
    }

    override suspend fun upsertBudget(budget: Budget) {
        budgetDao.upsertBudget(budget)
    }

    override suspend fun deleteBudget(budget: Budget) {
        budgetDao.deleteBudget(budget)
    }

}