package com.example.spendee.core.domain.util

import androidx.annotation.StringRes
import com.example.spendee.R
import com.example.spendee.core.domain.NotificationService
import com.example.spendee.core.presentation.util.Routes

class FakeNotificationService : NotificationService {

    val shownNotifications = mutableListOf<NotificationData>()

    data class NotificationData(
        @StringRes val titleResId: Int,
        @StringRes val textResId: Int,
        val route: String
    )

    private fun simulateShowNotification(@StringRes titleResId: Int, @StringRes textResId: Int, route: String) {
        shownNotifications.add(NotificationData(titleResId, textResId, route))
    }

    override fun showGoalReachedNotification() {
        simulateShowNotification(
            R.string.goal_reached,
            R.string.congratulations_you_have_reached_your_goal,
            Routes.GOALS
        )
    }

    override fun showGoalDeletedNotification() {
        simulateShowNotification(
            R.string.goal_deleted,
            R.string.you_failed_to_reach_your_goal_in_time_the_goal_has_been_deleted,
            Routes.GOALS
        )
    }

    override fun showBudgetExceededNotification() {
        simulateShowNotification(
            R.string.budget_exceeded,
            R.string.you_have_exceeded_your_budget,
            Routes.BUDGET
        )
    }

    fun wasNotificationShown(@StringRes titleResId: Int, @StringRes textResId: Int, route: String): Boolean {
        return shownNotifications.contains(NotificationData(titleResId, textResId, route))
    }

    fun clearNotifications() {
        shownNotifications.clear()
    }
}
