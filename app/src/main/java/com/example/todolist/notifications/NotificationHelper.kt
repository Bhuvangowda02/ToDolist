package com.example.todolist.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.*

class NotificationHelper(private val context: Context) {

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    fun scheduleNotification(taskTime: String, taskId: Long) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                !alarmManager.canScheduleExactAlarms()) {
                Log.w("NotificationHelper", "Exact alarms not permitted on this device.")
                return
            }

            val calendar = Calendar.getInstance()
            val timeParts = taskTime.split(":")
            val hour = timeParts[0].toInt()
            val minute = timeParts[1].split(" ")[0].toInt()
            val amPm = taskTime.split(" ")[1]

            val hourOfDay = when {
                amPm == "PM" && hour < 12 -> hour + 12
                amPm == "AM" && hour == 12 -> 0
                else -> hour
            }

            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)

            val intent = Intent(context, NotificationReceiver::class.java).apply {
                putExtra("taskId", taskId)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                taskId.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        } catch (e: SecurityException) {
            Log.e("NotificationHelper", "SecurityException: ${e.message}")
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun sendNotification(taskId: Long) {
        val notification = NotificationCompat.Builder(context, "task_channel")
            .setContentTitle("Task Reminders")
            .setSmallIcon(android.R.drawable.ic_notification_overlay)
            .setContentText("It's time to complete your task!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(taskId.toInt(), notification)
    }

}
