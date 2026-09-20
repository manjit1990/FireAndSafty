package com.yoga.firesafety.shared.util

import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNTimeIntervalNotificationTrigger

class IosNotificationService : NotificationService {

    override fun init() {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        center.requestAuthorizationWithOptions(
            UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
        ) { _, _ ->
            // Handle error or permission state if needed
        }
    }

    override fun showOverdueNotification(startCount: Int, completionCount: Int) {
        val content = UNMutableNotificationContent().apply {
            setTitle("⚠️ Appointment Action Required")
            setBody(when {
                startCount > 0 && completionCount > 0 -> "You have $startCount task(s) overdue to start and $completionCount task(s) overdue to complete."
                startCount > 0 -> "You have $startCount task(s) overdue to start. Please start them immediately."
                else -> "You have $completionCount task(s) overdue to complete. Please finish them immediately."
            })
            setSound(platform.UserNotifications.UNNotificationSound.defaultSound())
        }

        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(1.0, repeats = false)
        val request = UNNotificationRequest.requestWithIdentifier(
            "overdue_notification",
            content,
            trigger
        )

        UNUserNotificationCenter.currentNotificationCenter().addNotificationRequest(request) { _ ->
            // Handle error
        }
    }
}
