package com.example.spendee.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.spendee.data.dao.BalanceDao
import com.example.spendee.data.dao.BudgetDao
import com.example.spendee.data.dao.ExpenseDao
import com.example.spendee.data.dao.GoalDao
import com.example.spendee.data.entities.Balance
import com.example.spendee.data.entities.Budget
import com.example.spendee.data.entities.Converters
import com.example.spendee.data.entities.Expense
import com.example.spendee.data.entities.Goal

@Database(
    entities = [Balance::class, Budget::class, Expense::class, Goal::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SpendeeDatabase : RoomDatabase() {
    abstract fun balanceDao() : BalanceDao
    abstract fun budgetDao() : BudgetDao
    abstract fun expenseDao() : ExpenseDao
    abstract fun goalDao() : GoalDao
}