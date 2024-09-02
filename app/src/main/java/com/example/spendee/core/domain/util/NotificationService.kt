package com.example.spendee.core.domain.util

interface NotificationService {
    fun showGoalReachedNotification()
    fun showGoalDeletedNotification()
    fun showBudgetExceededNotification()
}