package com.example.spendee.workmanager

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.spendee.NotificationService
import com.example.spendee.data.entities.Budget
import com.example.spendee.data.repositories.BudgetRepository
import com.example.spendee.data.repositories.GoalRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.time.LocalDate

@HiltWorker
class DeadlinesCheckerWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val budgetRepository: BudgetRepository,
    private val goalsRepository: GoalRepository,
    private val notificationService: NotificationService
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            checkAndRenewBudget()
            checkAndDeleteGoals()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private suspend fun checkAndRenewBudget() = withContext(Dispatchers.IO) {
        val budget = budgetRepository.getBudget().firstOrNull() ?: return@withContext

        val currentDate = LocalDate.now()
        val endDate = budget.endDate

        if (currentDate.isAfter(endDate)) {
            val newStartDate = endDate.plusDays(1)
            val newEndDate = newStartDate.plusMonths(1).minusDays(1)

            val newBudget = Budget(
                totalAmount = budget.totalAmount,
                leftToSpend = budget.totalAmount,
                totalSpent = budget.totalAmount,
                startDate = newStartDate,
                endDate = newEndDate,
                isExceeded = budget.isExceeded,
                isExceedNotificationEnabled = budget.isExceedNotificationEnabled
            )
            budgetRepository.upsertBudget(newBudget)
        }
    }

    private suspend fun checkAndDeleteGoals() = withContext(Dispatchers.IO) {
        val goals = goalsRepository.getAllGoals().firstOrNull()
        if (goals.isNullOrEmpty()) {
            return@withContext
        }

        val currentDate = LocalDate.now()

        goals.forEach { goal ->
            if (currentDate.isAfter(goal.deadline)) {
                goalsRepository.deleteGoal(goal)
                notificationService.showGoalDeletedNotification()
            }
        }
    }
}