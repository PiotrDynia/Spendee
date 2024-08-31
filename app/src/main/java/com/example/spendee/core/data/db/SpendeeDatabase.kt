package com.example.spendee.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.spendee.feature_goals.data.data_source.GoalDao
import com.example.spendee.feature_goals.domain.model.Goal
import com.example.spendee.feature_budget.data.data_source.BudgetDao
import com.example.spendee.feature_budget.domain.model.Budget
import com.example.spendee.feature_current_balance.data.data_source.BalanceDao
import com.example.spendee.feature_current_balance.domain.model.Balance
import com.example.spendee.feature_expenses.data.data_source.ExpenseDao
import com.example.spendee.feature_expenses.domain.model.Expense

@Database(
    entities = [Balance::class, Budget::class, Expense::class, Goal::class],
    version = 7,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SpendeeDatabase : RoomDatabase() {
    abstract fun balanceDao() : BalanceDao
    abstract fun budgetDao() : BudgetDao
    abstract fun expenseDao() : ExpenseDao
    abstract fun goalDao() : GoalDao
}