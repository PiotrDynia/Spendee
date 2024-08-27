package com.example.spendee

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

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

    fun showGoalReachedNotification(route: String) {
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
            .setContentTitle("Goal reached!")
            .setContentText("Congratulations! You have reached your goal!")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        if (notificationManager.areNotificationsEnabled()) {
            notificationManager.notify(1, notification)
        }
    }
}
