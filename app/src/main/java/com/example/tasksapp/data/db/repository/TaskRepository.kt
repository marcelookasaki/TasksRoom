package com.example.tasksapp.data.db.repository

import com.example.tasksapp.data.db.dao.TaskDao
import com.example.tasksapp.data.db.entity.TaskEntity
import com.myo.tasksapp.data.model.Task

class TaskRepository (private val taskDao: TaskDao) {

    suspend fun getAllTasks() : List<Task>{
        return taskDao.getAllTasks()
    }

    suspend fun insertTask(taskEntity: TaskEntity) : Long{
        return taskDao.insertTask(taskEntity)
    }

    suspend fun deleteTask(id: Long){
        return taskDao.deleteTask(id)
    }

    suspend fun updateTask(taskEntity: TaskEntity) {
        return taskDao.updateTask(taskEntity.id, taskEntity.description, taskEntity.status)
    }
}