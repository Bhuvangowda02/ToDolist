package com.example.todolist.view

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.todolist.model.Task
import com.example.todolist.viewmodel.TaskViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import java.util.*

@Composable
fun MainScreen(viewModel: TaskViewModel) {
    var taskTitle by remember { mutableStateOf("") }
    var taskDescription by remember { mutableStateOf("") }
    var taskTime by remember { mutableStateOf("") }
    var hour by remember { mutableStateOf(0) }
    var minute by remember { mutableStateOf(0) }
    var isTimePickerVisible by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<Task?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {

        Text(
            text = "My To-Do List",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))


        OutlinedTextField(
            value = taskTitle,
            onValueChange = { taskTitle = it },
            label = { Text("Task Title") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { })
        )

        Spacer(modifier = Modifier.height(8.dp))


        OutlinedTextField(
            value = taskDescription,
            onValueChange = { taskDescription = it },
            label = { Text("Task Description") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = {  })
        )

        Spacer(modifier = Modifier.height(8.dp))


        TextButton(onClick = { isTimePickerVisible = true }) {
            Text(text = if (taskTime.isNotEmpty()) "Set Time: $taskTime" else "Set Time")
        }


        if (isTimePickerVisible) {
            val context = LocalContext.current
            TimePickerDialog(
                context,
                { _, selectedHour, selectedMinute ->
                    hour = selectedHour
                    minute = selectedMinute
                    val amPm = if (hour < 12) "AM" else "PM"
                    val displayHour = if (hour % 12 == 0) 12 else hour % 12
                    taskTime = "$displayHour:$minute $amPm"
                    isTimePickerVisible = false
                },
                Calendar.getInstance()[Calendar.HOUR_OF_DAY],
                Calendar.getInstance()[Calendar.MINUTE],
                false
            ).show()
        }

        Spacer(modifier = Modifier.height(16.dp))


        OutlinedTextField(
            value = taskTime,
            onValueChange = { taskTime = it },
            label = { Text("Set Reminder Time") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (taskTitle.isNotBlank()) {
                        if (isEditing && editingTask != null) {

                            viewModel.updateTask(editingTask!!, taskTitle, taskDescription, taskTime)
                        } else {

                            viewModel.addTask(taskTitle, taskDescription, taskTime)
                        }

                        taskTitle = ""
                        taskDescription = ""
                        taskTime = ""
                        isEditing = false
                        editingTask = null
                    }
                }
            )
        )

        Spacer(modifier = Modifier.height(16.dp))


        Button(
            onClick = {
                if (taskTitle.isNotBlank() && taskTime.isNotBlank()) {
                    if (isEditing && editingTask != null) {

                        viewModel.updateTask(editingTask!!, taskTitle, taskDescription, taskTime)
                    } else {

                        viewModel.addTask(taskTitle, taskDescription, taskTime)
                    }
                    taskTitle = ""
                    taskDescription = ""
                    taskTime = ""
                    isEditing = false
                    editingTask = null
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isEditing) "Save Changes" else "Add Task")
        }

        Spacer(modifier = Modifier.height(16.dp))


        LazyColumn {
            items(viewModel.tasks) { task ->
                TaskItem(
                    task = task,
                    onCheckedChange = { viewModel.toggleTask(task) },
                    onDelete = { viewModel.deleteTask(task) },
                    onEdit = {

                        taskTitle = task.title
                        taskDescription = task.description
                        taskTime = task.time
                        isEditing = true
                        editingTask = task
                    }
                )
            }
        }
    }
}

@Composable
fun TaskItem(task: Task, onCheckedChange: () -> Unit, onDelete: () -> Unit, onEdit: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Checkbox(
                checked = task.isDone,
                onCheckedChange = { onCheckedChange() }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(text = task.title, style = MaterialTheme.typography.bodyLarge)
                Text(text = task.description, style = MaterialTheme.typography.bodySmall)
                Text(text = task.time, style = MaterialTheme.typography.bodySmall)
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onEdit) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Task")
            }
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Task")
            }
        }
    }
}
