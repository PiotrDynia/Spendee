package com.example.spendee.feature_budget.data.repository

import com.example.spendee.feature_budget.data.data_source.BudgetDao
import com.example.spendee.feature_budget.domain.model.Budget
import com.example.spendee.feature_budget.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow

class BudgetRepositoryImpl(
    private val budgetDao: BudgetDao
) : BudgetRepository {
    override fun getBudget(): Flow<Budget> {
        return budgetDao.getBudget()
    }

    override suspend fun upsertBudget(budget: Budget) {
        budgetDao.upsertBudget(budget)
    }

    override suspend fun deleteBudget(budget: Budget) {
        budgetDao.deleteBudget(budget)
    }

}