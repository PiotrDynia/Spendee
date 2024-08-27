package com.example.spendee.workmanager

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.spendee.data.entities.Budget
import com.example.spendee.data.repositories.BudgetRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.time.LocalDate

@HiltWorker
class BudgetRenewalWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val budgetRepository: BudgetRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Log.d("BudgetRenewalWorker", "Worker started.")
            checkAndRenewBudget()
            Log.d("BudgetRenewalWorker", "Worker completed successfully.")
            Result.success()
        } catch (e: Exception) {
            Log.e("BudgetRenewalWorker", "Worker failed with exception: ${e.message}", e)
            Result.retry()
        }
    }

    private suspend fun checkAndRenewBudget() = withContext(Dispatchers.IO) {
        val budget = budgetRepository.getBudget().firstOrNull()
        if (budget == null) {
            Log.d("BudgetRenewalWorker", "No budget found, skipping renewal.")
            return@withContext
        }

        val currentDate = LocalDate.now()
        val endDate = budget.endDate
        Log.d("BudgetRenewalWorker", "Current date: $currentDate, Budget end date: $endDate")

        if (currentDate.isAfter(endDate)) {
            Log.d("BudgetRenewalWorker", "Renewing budget...")
            val newStartDate = endDate.plusDays(1)
            val newEndDate = newStartDate.plusMonths(1).minusDays(1)

            val newBudget = Budget(
                totalAmount = budget.totalAmount,
                currentAmount = budget.totalAmount,
                startDate = newStartDate,
                endDate = newEndDate,
                isExceedNotificationEnabled = budget.isExceedNotificationEnabled,
                isReach80PercentNotificationEnabled = budget.isReach80PercentNotificationEnabled
            )
            budgetRepository.upsertBudget(newBudget)
            Log.d("BudgetRenewalWorker", "Budget renewed successfully.")
        } else {
            Log.d("BudgetRenewalWorker", "No renewal needed, current budget is still active.")
        }
    }
}