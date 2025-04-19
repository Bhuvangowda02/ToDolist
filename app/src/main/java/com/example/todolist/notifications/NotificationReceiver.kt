package com.example.todolist.notifications

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission

class NotificationReceiver : BroadcastReceiver() {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent?) {
        val taskId = intent?.getLongExtra("taskId", 0L) ?: return
        val notificationHelper = NotificationHelper(context)
        notificationHelper.sendNotification(taskId)
    }
}
