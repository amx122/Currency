package com.example.kursova_mobile.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {
    @Query("SELECT * FROM exchange_rates ORDER BY isFavorite DESC, currencyCode ASC")
    fun getAllRates(): Flow<List<ExchangeRateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRate(rate: ExchangeRateEntity)

    @Query("UPDATE exchange_rates SET isFavorite = :isFav WHERE currencyCode = :code")
    suspend fun updateFavorite(code: String, isFav: Boolean)

    @Query("SELECT * FROM exchange_rates")
    suspend fun getRatesList(): List<ExchangeRateEntity>

    @Query("SELECT * FROM alert_rules")
    suspend fun getAlertsList(): List<AlertEntity>

    @Query("SELECT * FROM alert_rules")
    fun getAllAlerts(): Flow<List<AlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertEntity)

    @Delete
    suspend fun deleteAlert(alert: AlertEntity)

    @Query("SELECT * FROM error_logs ORDER BY timestamp DESC")
    fun getErrorLogs(): Flow<List<ErrorLogEntity>>

    @Insert
    suspend fun logError(error: ErrorLogEntity)
    @Insert
    suspend fun insertConversion(conversion: ConversionEntity)

    @Query("SELECT * FROM conversion_history ORDER BY timestamp DESC LIMIT 10")
    fun getConversionHistory(): Flow<List<ConversionEntity>>
}