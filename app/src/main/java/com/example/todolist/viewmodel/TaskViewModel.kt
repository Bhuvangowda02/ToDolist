package com.example.todolist.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import com.example.todolist.model.Task
import com.example.todolist.notifications.NotificationHelper
import com.example.todolist.repository.TaskRepository

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TaskRepository()
    private val notificationHelper = NotificationHelper(application)
    var tasks = mutableStateListOf<Task>()
        private set

    init {
        tasks.addAll(repository.getTasks())
    }

    @SuppressLint("ScheduleExactAlarm")
    fun addTask(title: String, description: String, time: String) {
        val task = Task(title = title, description = description, time = time)
        repository.addTask(task)
        tasks.add(task)
        notificationHelper.scheduleNotification(time, task.id)
    }

    fun deleteTask(task: Task) {
        repository.deleteTask(task)
        tasks.remove(task)
    }

    fun toggleTask(task: Task) {
        val updatedTask = task.copy(isDone = !task.isDone)
        repository.updateTask(updatedTask)
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            tasks[index] = updatedTask
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    @Suppress("MissingPermission")
    fun updateTask(task: Task, newTitle: String, newDescription: String, newTime: String) {
        val updatedTask = task.copy(title = newTitle, description = newDescription, time = newTime)
        repository.updateTask(updatedTask)
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            tasks[index] = updatedTask
        }
        notificationHelper.scheduleNotification(newTime, updatedTask.id)
    }

}
