package ru.kgu.taskcalendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = TaskRepository(database.taskDao())

    val allTasks: StateFlow<List<Task>> = repository.getAllTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addTask(title: String, description: String, date: String) {
        viewModelScope.launch {
            val newTask = Task(
                title = title,
                description = description,
                date = date
            )
            repository.insertTask(newTask)
        }
    }
}