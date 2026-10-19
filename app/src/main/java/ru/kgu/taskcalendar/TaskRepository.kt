package ru.kgu.taskcalendar


import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {

    fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks()
    }

    suspend fun insertTask(task: Task) {
        taskDao.insert(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.delete(task)
    }
}
