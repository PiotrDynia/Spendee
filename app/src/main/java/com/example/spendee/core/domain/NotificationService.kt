package com.example.spendee.core.domain

import androidx.annotation.StringRes

interface NotificationService {
    fun showGoalReachedNotification()
    fun showGoalDeletedNotification()
    fun showBudgetExceededNotification()
}