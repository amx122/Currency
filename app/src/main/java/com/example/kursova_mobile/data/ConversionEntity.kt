package com.example.kursova_mobile.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversion_history")
data class ConversionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fromCurrency: String,
    val toCurrency: String,
    val amountFrom: Double,
    val amountTo: Double,
    val timestamp: Long = System.currentTimeMillis()
)