package com.example.kursova_mobile.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alert_rules")
data class AlertEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val currencyCode: String,
    val targetRate: Double,
    val isHigher: Boolean
)