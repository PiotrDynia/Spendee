package com.example.spendee.core.domain.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.spendee.MainActivity
import com.example.spendee.R
import com.example.spendee.core.domain.NotificationService
import com.example.spendee.core.presentation.util.Routes

class NotificationServiceImpl(private val context: Context) : NotificationService {

    private val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(context)
    private val channelId = "Main Channel ID"

    init {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Main Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(@StringRes titleResId: Int, @StringRes textResId: Int, route: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("route", route)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentTitle(context.getString(titleResId))
            .setContentText(context.getString(textResId))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        if (notificationManager.areNotificationsEnabled()) {
            notificationManager.notify(1, notification)
        }
    }

    override fun showGoalReachedNotification() {
        showNotification(
            R.string.goal_reached,
            R.string.congratulations_you_have_reached_your_goal,
            Routes.GOALS
        )
    }

    override fun showGoalDeletedNotification() {
        showNotification(
            R.string.goal_deleted,
            R.string.you_failed_to_reach_your_goal_in_time_the_goal_has_been_deleted,
            Routes.GOALS
        )
    }

    override fun showBudgetExceededNotification() {
        showNotification(
            R.string.budget_exceeded,
            R.string.you_have_exceeded_your_budget,
            Routes.BUDGET
        )
    }
}