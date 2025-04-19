package com.example.todolist.model

data class Task(
    val title: String,
    val description: String = "",
    val time: String = "",
    val isDone: Boolean = false,
    val id: Long = System.currentTimeMillis()
)
