package com.example.spendee

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.spendee.util.Routes

class NotificationService(private val context: Context) {

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

    fun showGoalReachedNotification() {
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("route", Routes.GOALS)
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
            .setContentTitle(context.getString(R.string.goal_reached))
            .setContentText(context.getString(R.string.congratulations_you_have_reached_your_goal))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        if (notificationManager.areNotificationsEnabled()) {
            notificationManager.notify(1, notification)
        }
    }

    fun showGoalDeletedNotification() {
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("route", Routes.GOALS)
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
            .setContentTitle(context.getString(R.string.goal_deleted))
            .setContentText(context.getString(R.string.you_failed_to_reach_your_goal_in_time_the_goal_has_been_deleted))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        if (notificationManager.areNotificationsEnabled()) {
            notificationManager.notify(1, notification)
        }
    }

    fun showBudgetExceededNotification() {
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("route", Routes.BUDGET)
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
            .setContentTitle(context.getString(R.string.budget_exceeded))
            .setContentText(context.getString(R.string.you_have_exceeded_your_budget))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        if (notificationManager.areNotificationsEnabled()) {
            notificationManager.notify(1, notification)
        }
    }
}
