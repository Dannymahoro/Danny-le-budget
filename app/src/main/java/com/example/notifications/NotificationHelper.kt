package com.example.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity

object NotificationHelper {

    private const val CHANNEL_WEEKLY = "danny_weekly_summary"
    private const val CHANNEL_ANNOUNCEMENTS = "danny_announcements"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val weeklyChannel = NotificationChannel(
                CHANNEL_WEEKLY,
                "Bilan Hebdomadaire Danny",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Résumé automatique des dépenses de la semaine (Dimanche 20h)"
                enableVibration(true)
            }

            val announcementsChannel = NotificationChannel(
                CHANNEL_ANNOUNCEMENTS,
                "Annonces & Bons Plans Danny",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications d'annonces, nouveautés et bons plans étudiants"
                enableVibration(true)
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.createNotificationChannel(weeklyChannel)
            manager?.createNotificationChannel(announcementsChannel)
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun showWeeklySummaryNotification(
        context: Context,
        totalSpent: Double,
        breakdownText: String
    ) {
        if (!hasNotificationPermission(context)) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            1001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val formattedTotal = String.format(java.util.Locale.FRANCE, "%.2f €", totalSpent)
        val content = if (totalSpent > 0) {
            "Total de la semaine : $formattedTotal. $breakdownText"
        } else {
            "Bravo ! 0 € dépensé cette semaine. Continue ainsi pour garder le cap !"
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_WEEKLY)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("📊 Danny le budget : Bilan du dimanche 20h")
            .setContentText("Total de la semaine : $formattedTotal")
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(2001, builder.build())
        } catch (_: SecurityException) {}
    }

    fun showAdminAnnouncementNotification(
        context: Context,
        title: String,
        message: String,
        segment: String
    ) {
        if (!hasNotificationPermission(context)) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            1002,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val fullTitle = "📢 Danny [$segment] : $title"

        val builder = NotificationCompat.Builder(context, CHANNEL_ANNOUNCEMENTS)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(fullTitle)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            val notificationId = (System.currentTimeMillis() % 10000).toInt() + 3000
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (_: SecurityException) {}
    }
}
