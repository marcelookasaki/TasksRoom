package com.example.tasksapp.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tasksapp.data.model.Status

@Entity(tableName = "task_table")
class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val description: String,
    val status: Status
)