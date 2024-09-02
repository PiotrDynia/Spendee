package com.example.spendee.feature_current_balance.domain.use_case

import com.example.spendee.R
import com.example.spendee.core.domain.NotificationService
import com.example.spendee.feature_current_balance.domain.model.Balance
import com.example.spendee.feature_current_balance.domain.model.InvalidBalanceException
import com.example.spendee.feature_current_balance.domain.repository.BalanceRepository
import com.example.spendee.feature_goals.domain.model.Goal
import com.example.spendee.feature_goals.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class UpdateBalance(
    private val balanceRepository: BalanceRepository,
    private val goalsRepository: GoalRepository,
    private val notificationService: NotificationService
) {
    @Throws(InvalidBalanceException::class)
    suspend operator fun invoke(currentAmount: String) {
        if (currentAmount.isBlank()) {
            throw InvalidBalanceException(R.string.amount_cant_be_empty)
        }
        balanceRepository.upsertBalance(Balance(amount = currentAmount.toDouble()))
        updateGoalsIfNeeded(currentAmount = currentAmount.toDouble(), goals = goalsRepository.getAllGoals())
    }

    private suspend fun updateGoalsIfNeeded(currentAmount: Double, goals: Flow<List<Goal>>) {
        val goalList = goals.firstOrNull() ?: return

        goalList.forEach { goal ->
            if (currentAmount >= goal.targetAmount && !goal.isReached) {
                goal.isReached = true
                if (goal.isReachedNotificationEnabled) {
                    goal.isReachedNotificationEnabled = false
                    notificationService.showGoalReachedNotification()
                }
                goalsRepository.upsertGoal(goal)
            }
        }
    }
}