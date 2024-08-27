package com.example.spendee.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.spendee.NotificationService
import com.example.spendee.data.SpendeeDatabase
import com.example.spendee.data.repositories.BalanceRepository
import com.example.spendee.data.repositories.BalanceRepositoryImpl
import com.example.spendee.data.repositories.BudgetRepository
import com.example.spendee.data.repositories.BudgetRepositoryImpl
import com.example.spendee.data.repositories.ExpenseRepository
import com.example.spendee.data.repositories.ExpenseRepositoryImpl
import com.example.spendee.data.repositories.GoalRepository
import com.example.spendee.data.repositories.GoalRepositoryImpl
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