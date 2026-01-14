package com.example.kursova_mobile.data

import android.util.Log
import com.example.kursova_mobile.network.ApiService
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class CurrencyRepository(
    private val apiService: ApiService,
    private val currencyDao: CurrencyDao
) {

    fun getRatesFromDb(): Flow<List<ExchangeRateEntity>> = currencyDao.getAllRates()
    fun getAlertsFromDb(): Flow<List<AlertEntity>> = currencyDao.getAllAlerts()
    fun getErrorLogsFromDb(): Flow<List<ErrorLogEntity>> = currencyDao.getErrorLogs()

    fun getConversionHistory(): Flow<List<ConversionEntity>> = currencyDao.getConversionHistory()

    suspend fun getRatesNow(): List<ExchangeRateEntity> = currencyDao.getRatesList()
    suspend fun getAlertsNow(): List<AlertEntity> = currencyDao.getAlertsList()

    suspend fun refreshRates() {
        val nbuList = apiService.getNbuRates()

        val entities = nbuList.map { nbuItem ->
            ExchangeRateEntity(
                currencyCode = nbuItem.code,
                rate = nbuItem.rate,
                timestamp = System.currentTimeMillis()
            )
        }

        currencyDao.insertRate(ExchangeRateEntity("UAH", 1.0, System.currentTimeMillis()))
        entities.forEach { currencyDao.insertRate(it) }
    }

    suspend fun toggleFavorite(code: String, isFav: Boolean) = currencyDao.updateFavorite(code, isFav)
    suspend fun saveAlert(alert: AlertEntity) = currencyDao.insertAlert(alert)
    suspend fun deleteAlert(alert: AlertEntity) = currencyDao.deleteAlert(alert)
    suspend fun saveConversion(conversion: ConversionEntity) = currencyDao.insertConversion(conversion)

    suspend fun logError(msg: String) = currencyDao.logError(ErrorLogEntity(errorMessage = msg))

    suspend fun getHistoryForWeek(currencyCode: String): List<Entry> {
        val entries = mutableListOf<Entry>()
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

        for (i in 6 downTo 0) {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val dateStr = dateFormat.format(calendar.time)

            try {
                val response = apiService.getRateByDate(currencyCode, dateStr)
                if (response.isNotEmpty()) {
                    val rate = response[0].rate.toFloat()
                    entries.add(Entry((6 - i).toFloat(), rate))
                }
            } catch (e: Exception) {
                Log.e("HISTORY", "Error loading date $dateStr: ${e.message}")
            }
        }
        return entries
    }
}