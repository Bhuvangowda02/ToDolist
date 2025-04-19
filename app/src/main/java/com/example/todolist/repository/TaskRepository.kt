package com.example.todolist.repository

import com.example.todolist.model.Task

class TaskRepository {
    private val tasklist = mutableListOf<Task>()

    fun getTasks(): List<Task> = tasklist

    fun addTask(task: Task) {
        tasklist.add(task)
    }

    fun deleteTask(task: Task) {
        tasklist.remove(task)
    }

    fun updateTask(task: Task) {
        val index = tasklist.indexOfFirst { it.id == task.id }
        if (index != -1) {
            tasklist[index] = task
        }
    }
}
