package com.example.spendee.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.spendee.core.domain.util.NotificationService
import com.example.spendee.core.data.db.SpendeeDatabase
import com.example.spendee.feature_current_balance.domain.repository.BalanceRepository
import com.example.spendee.feature_current_balance.data.repository.BalanceRepositoryImpl
import com.example.spendee.feature_budget.domain.repository.BudgetRepository
import com.example.spendee.feature_budget.data.repository.BudgetRepositoryImpl
import com.example.spendee.feature_expenses.domain.repository.ExpenseRepository
import com.example.spendee.feature_expenses.data.repository.ExpenseRepositoryImpl
import com.example.spendee.feature_goals.domain.repository.GoalRepository
import com.example.spendee.feature_goals.data.repository.GoalRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application) : SpendeeDatabase {
        return Room.databaseBuilder(
            app,
            SpendeeDatabase::class.java,
            "spendee_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideNotificationService(@ApplicationContext context: Context): NotificationService {
        return NotificationService(context)
    }

    @Provides
    @Singleton
    fun provideBudgetRepository(db: SpendeeDatabase) : BudgetRepository {
        return BudgetRepositoryImpl(db.budgetDao())
    }

    @Provides
    @Singleton
    fun provideExpenseRepository(db: SpendeeDatabase) : ExpenseRepository {
        return ExpenseRepositoryImpl(db.expenseDao())
    }

    @Provides
    @Singleton
    fun provideBalanceRepository(db: SpendeeDatabase) : BalanceRepository {
        return BalanceRepositoryImpl(db.balanceDao())
    }
    @Provides
    @Singleton
    fun provideGoalRepository(db: SpendeeDatabase) : GoalRepository {
        return GoalRepositoryImpl(db.goalDao())
    }

}