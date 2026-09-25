package com.learningapp.firesafetyservicemanagement

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import com.yoga.firesafety.shared.domain.model.checkIfOverdue
import com.yoga.firesafety.shared.domain.model.checkIfCompletionOverdue
import com.yoga.firesafety.shared.domain.repository.WorkOrderRepository
import com.yoga.firesafety.shared.util.NotificationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlinx.datetime.Instant as KotlinxInstant
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class OverdueTaskReceiver : BroadcastReceiver(), KoinComponent {

    private val repository: WorkOrderRepository by inject()
    private val notificationService: NotificationService by inject()

    override fun onReceive(context: Context, intent: Intent) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Get the current list of work orders (this assumes they are cached or accessible)
                val workOrders = repository.getWorkOrders().first()
                val now = KotlinxInstant.fromEpochMilliseconds(Clock.System.now().toEpochMilliseconds())
                val startOverdueCount = workOrders.count { it.checkIfOverdue(now) }
                val completionOverdueCount = workOrders.count { it.checkIfCompletionOverdue(now) }

                if (startOverdueCount > 0 || completionOverdueCount > 0) {
                    notificationService.showOverdueNotification(startOverdueCount, completionOverdueCount)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                // Reschedule alarm for 5 minutes later safely
                scheduleNextAlarm(context)
            }
        }
    }

    private fun scheduleNextAlarm(context: Context) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
            val intent = Intent(context, OverdueTaskReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context, 
                0, 
                intent, 
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val interval = 5 * 60 * 1000L
            val triggerAt = SystemClock.elapsedRealtime() + interval

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        triggerAt,
                        pendingIntent
                    )
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        triggerAt,
                        pendingIntent
                    )
                } else {
                    alarmManager.set(
                        AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        triggerAt,
                        pendingIntent
                    )
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    triggerAt,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    triggerAt,
                    pendingIntent
                )
            }
        } catch (_: SecurityException) {
            // Fallback to inexact alarm if exact alarm permission is denied
            try {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
                val intent = Intent(context, OverdueTaskReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(
                    context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                val triggerAt = SystemClock.elapsedRealtime() + (5 * 60 * 1000L)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        triggerAt,
                        pendingIntent
                    )
                } else {
                    alarmManager.set(
                        AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        triggerAt,
                        pendingIntent
                    )
                }
            } catch (_: Exception) {
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
