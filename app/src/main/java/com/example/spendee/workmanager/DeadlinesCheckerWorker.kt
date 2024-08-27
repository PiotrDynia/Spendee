package com.example.spendee.workmanager

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
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
    private val goalsRepository: GoalRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Log.d("DeadlinesCheckerWorker", "Worker started.")
            checkAndRenewBudget()
            checkAndDeleteGoals()
            Log.d("DeadlinesCheckerWorker", "Worker completed successfully.")
            Result.success()
        } catch (e: Exception) {
            Log.e("DeadlinesCheckerWorker", "Worker failed with exception: ${e.message}", e)
            Result.retry()
        }
    }

    private suspend fun checkAndRenewBudget() = withContext(Dispatchers.IO) {
        val budget = budgetRepository.getBudget().firstOrNull()
        if (budget == null) {
            Log.d("DeadlinesCheckerWorker", "No budget found, skipping renewal.")
            return@withContext
        }

        val currentDate = LocalDate.now()
        val endDate = budget.endDate
        Log.d("DeadlinesCheckerWorker", "Current date: $currentDate, Budget end date: $endDate")

        if (currentDate.isAfter(endDate)) {
            Log.d("DeadlinesCheckerWorker", "Renewing budget...")
            val newStartDate = endDate.plusDays(1)
            val newEndDate = newStartDate.plusMonths(1).minusDays(1)

            val newBudget = Budget(
                totalAmount = budget.totalAmount,
                currentAmount = budget.totalAmount,
                startDate = newStartDate,
                endDate = newEndDate,
                isExceeded = budget.isExceeded,
                isExceedNotificationEnabled = budget.isExceedNotificationEnabled
            )
            budgetRepository.upsertBudget(newBudget)
            Log.d("DeadlinesCheckerWorker", "Budget renewed successfully.")
        } else {
            Log.d("DeadlinesCheckerWorker", "No renewal needed, current budget is still active.")
        }
    }

    private suspend fun checkAndDeleteGoals() = withContext(Dispatchers.IO) {
        val goals = goalsRepository.getAllGoals().firstOrNull()
        if (goals.isNullOrEmpty()) {
            Log.d("DeadlinesCheckerWorker", "No goals found, skipping check.")
            return@withContext
        }

        val currentDate = LocalDate.now()

        goals.forEach { goal ->
            Log.d("DeadlinesCheckerWorker", "Current date: $currentDate, Goal deadline: ${goal.deadline}")
            if (currentDate.isAfter(goal.deadline)) {
                goalsRepository.deleteGoal(goal)
                Log.d("DeadlinesCheckerWorker", "Goal deleted successfully.")
                // TODO add notification
            }
        }
    }
}