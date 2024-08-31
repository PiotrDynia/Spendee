package com.example.spendee.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.spendee.core.data.db.SpendeeDatabase
import com.example.spendee.core.domain.util.NotificationService
import com.example.spendee.feature_budget.data.repository.BudgetRepositoryImpl
import com.example.spendee.feature_budget.domain.repository.BudgetRepository
import com.example.spendee.feature_budget.domain.use_case.AddBudget
import com.example.spendee.feature_budget.domain.use_case.BudgetUseCases
import com.example.spendee.feature_budget.domain.use_case.DeleteBudget
import com.example.spendee.feature_budget.domain.use_case.GetBudget
import com.example.spendee.feature_current_balance.data.repository.BalanceRepositoryImpl
import com.example.spendee.feature_current_balance.domain.repository.BalanceRepository
import com.example.spendee.feature_current_balance.domain.use_case.BalanceUseCases
import com.example.spendee.feature_current_balance.domain.use_case.GetCurrentBalance
import com.example.spendee.feature_current_balance.domain.use_case.UpdateBalance
import com.example.spendee.feature_expenses.data.repository.ExpenseRepositoryImpl
import com.example.spendee.feature_expenses.domain.repository.ExpenseRepository
import com.example.spendee.feature_expenses.domain.use_case.AddExpense
import com.example.spendee.feature_expenses.domain.use_case.DeleteExpense
import com.example.spendee.feature_expenses.domain.use_case.ExpensesUseCases
import com.example.spendee.feature_expenses.domain.use_case.GetExpense
import com.example.spendee.feature_expenses.domain.use_case.GetExpenses
import com.example.spendee.feature_goals.data.repository.GoalRepositoryImpl
import com.example.spendee.feature_goals.domain.repository.GoalRepository
import com.example.spendee.feature_goals.domain.use_case.AddGoal
import com.example.spendee.feature_goals.domain.use_case.DeleteGoal
import com.example.spendee.feature_goals.domain.use_case.GetGoal
import com.example.spendee.feature_goals.domain.use_case.GetGoals
import com.example.spendee.feature_goals.domain.use_case.GoalsUseCases
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
    fun provideBudgetUseCases(budgetRepository: BudgetRepository, expenseRepository: ExpenseRepository) : BudgetUseCases {
        return BudgetUseCases(
            addBudget = AddBudget(budgetRepository, expenseRepository),
            deleteBudget = DeleteBudget(budgetRepository),
            getBudget = GetBudget(budgetRepository)
        )
    }

    @Provides
    @Singleton
    fun provideExpenseRepository(db: SpendeeDatabase) : ExpenseRepository {
        return ExpenseRepositoryImpl(db.expenseDao())
    }

    @Provides
    @Singleton
    fun provideExpensesUseCases(
        expenseRepository: ExpenseRepository,
        balanceRepository: BalanceRepository,
        budgetRepository: BudgetRepository,
        notificationService: NotificationService
    ) : ExpensesUseCases {
        return ExpensesUseCases(
            getExpenses = GetExpenses(expenseRepository),
            deleteExpense = DeleteExpense(expenseRepository, budgetRepository, balanceRepository),
            getExpense = GetExpense(expenseRepository),
            addExpense = AddExpense(expenseRepository, budgetRepository, balanceRepository, notificationService)
        )
    }

    @Provides
    @Singleton
    fun provideBalanceRepository(db: SpendeeDatabase) : BalanceRepository {
        return BalanceRepositoryImpl(db.balanceDao())
    }

    @Provides
    @Singleton
    fun provideBalanceUseCases(
        balanceRepository: BalanceRepository,
        goalsRepository: GoalRepository,
        notificationService: NotificationService
    ) : BalanceUseCases {
        return BalanceUseCases(
            getBalance = GetCurrentBalance(balanceRepository),
            updateBalance = UpdateBalance(balanceRepository, goalsRepository, notificationService)
        )
    }

    @Provides
    @Singleton
    fun provideGoalRepository(db: SpendeeDatabase) : GoalRepository {
        return GoalRepositoryImpl(db.goalDao())
    }

    @Provides
    @Singleton
    fun provideGoalsUseCases(
        balanceRepository: BalanceRepository,
        goalsRepository: GoalRepository
    ) : GoalsUseCases {
        return GoalsUseCases(
            getGoals = GetGoals(goalsRepository),
            getGoal = GetGoal(goalsRepository),
            addGoal = AddGoal(goalsRepository, balanceRepository),
            deleteGoal = DeleteGoal(goalsRepository)
        )
    }

}