package com.yoga.firesafety.shared.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class AndroidNotificationService(private val context: Context) : NotificationService {

    companion object {
        const val CHANNEL_ID = "overdue_appointments"
        const val NOTIFICATION_ID = 1001
    }

    override fun init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Overdue Appointments"
            val descriptionText = "Notifications for appointments that have not been started on time"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun showOverdueNotification(startCount: Int, completionCount: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent? = intent?.let {
            PendingIntent.getActivity(context, 0, it, PendingIntent.FLAG_IMMUTABLE)
        }

        val message = when {
            startCount > 0 && completionCount > 0 -> "You have $startCount task(s) overdue to start and $completionCount task(s) overdue to complete."
            startCount > 0 -> "You have $startCount task(s) overdue to start. Please start them immediately."
            else -> "You have $completionCount task(s) overdue to complete. Please finish them immediately."
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("⚠️ Appointment Action Required")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setOngoing(false)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))

        with(NotificationManagerCompat.from(context)) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                notify(NOTIFICATION_ID, builder.build())
            }
        }
    }
}
