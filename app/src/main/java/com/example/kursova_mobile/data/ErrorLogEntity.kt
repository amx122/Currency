package com.example.kursova_mobile.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "error_logs")
data class ErrorLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val errorMessage: String,
    val timestamp: Long = System.currentTimeMillis()
)