package com.learningapp.firesafetyservicemanagement

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import com.yoga.firesafety.shared.domain.model.checkIfOverdue
import com.yoga.firesafety.shared.domain.model.checkIfCompletionOverdue
import com.yoga.firesafety.shared.domain.repository.WorkOrderRepository
import com.yoga.firesafety.shared.util.NotificationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock as KtClock
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
                val now = KtClock.System.now()
                val startOverdueCount = workOrders.count { it.checkIfOverdue(now) }
                val completionOverdueCount = workOrders.count { it.checkIfCompletionOverdue(now) }

                if (startOverdueCount > 0 || completionOverdueCount > 0) {
                    notificationService.showOverdueNotification(startOverdueCount, completionOverdueCount)
                }

                // Reschedule alarm for 5 minutes later
                scheduleNextAlarm(context)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun scheduleNextAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, OverdueTaskReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 
            0, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 5 minutes in milliseconds
        val interval = 5 * 60 * 1000L
        val triggerAt = SystemClock.elapsedRealtime() + interval

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            triggerAt,
            pendingIntent
        )
    }
}
