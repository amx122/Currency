package com.example.kursova_mobile.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exchange_rates")
data class ExchangeRateEntity(
    @PrimaryKey val currencyCode: String,
    val rate: Double,
    val timestamp: Long,
    val isFavorite: Boolean = false
)