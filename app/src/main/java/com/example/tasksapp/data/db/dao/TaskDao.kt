package com.example.tasksapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tasksapp.data.db.entity.TaskEntity
import com.example.tasksapp.data.model.Status
import com.myo.tasksapp.data.model.Task

@Dao
interface TaskDao {
    @Query("SELECT * FROM task_table ORDER BY id DESC")
    suspend fun getAllTasks() : List<Task>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTask(taskEntity: TaskEntity) : Long

    @Query("DELETE FROM task_table WHERE id = :id")
    suspend fun deleteTask(id: Long)

    @Query("UPDATE task_table SET description = :description, status = :status WHERE id = :id")
    suspend fun updateTask(
        id: Long,
        description: String,
        status: Status
    )
}