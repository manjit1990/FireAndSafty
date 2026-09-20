package com.yoga.firesafety.shared.util

interface NotificationService {
    fun showOverdueNotification(startCount: Int, completionCount: Int)
    fun init()
}
