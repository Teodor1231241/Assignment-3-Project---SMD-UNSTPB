package com.phishguard.app.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.phishguard.app.MainActivity

object NotificationHelper {
    private const val CHANNEL_ID = "phishguard_alerts"

    fun showPhishWarning(context: Context, sender: String, body: String, result: DetectionResult) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            "PhishGuard Threat Alerts",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Triggers immediately when automated phishing footprints are detected."
        }
        notificationManager.createNotificationChannel(channel)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // Standardized Immutable Flag matching API 34/35 runtime rules
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setContentTitle("[!] ${result.riskLevel} Threat Blocked")
            .setContentText("Source: $sender")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Message: \"$body\"\n\nReason: ${result.reason}"))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // Use a stable but unique ID to avoid collisions and overflows
        val notificationId = (System.currentTimeMillis() % 1000000).toInt()
        notificationManager.notify(notificationId, notification)
    }
}