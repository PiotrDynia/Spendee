package com.example.spendee.feature_goals.domain.use_case

import com.example.spendee.R
import com.example.spendee.feature_current_balance.domain.repository.BalanceRepository
import com.example.spendee.feature_goals.domain.model.Goal
import com.example.spendee.feature_goals.domain.model.InvalidGoalException
import com.example.spendee.feature_goals.domain.repository.GoalRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class AddGoal(
    private val goalRepository: GoalRepository,
    private val balanceRepository: BalanceRepository
) {

    @Throws(InvalidGoalException::class)
    suspend operator fun invoke(goal: Goal) {
        val balance = balanceRepository.getBalance().first()
        if (isInputValid(goal)) {
            val goalWithBalance = goal.copy(
                isReached = balance.amount >= goal.targetAmount
            )
            goalRepository.upsertGoal(goalWithBalance)
        }
    }

    private fun isInputValid(goal: Goal): Boolean {
        return when {
            goal.targetAmount == 0.0 -> {
                throw InvalidGoalException(R.string.target_amount_cant_be_empty)
            }
            goal.description.isBlank() -> {
                throw InvalidGoalException(R.string.description_cant_be_empty)
            }
            goal.deadline.isBefore(LocalDate.now().plusDays(1)) && !goal.isReached -> {
                throw InvalidGoalException(R.string.deadline_should_be_after_todays_date)
            }
            else -> true
        }
    }
}