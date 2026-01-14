package com.example.kursova_mobile.ui

import android.app.Application
import androidx.lifecycle.*
import com.example.kursova_mobile.data.*
import com.example.kursova_mobile.network.RetrofitInstance
import com.example.kursova_mobile.utils.NotificationHelper
import com.example.kursova_mobile.utils.PrefsManager
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CurrencyRepository
    val allRates: LiveData<List<ExchangeRateEntity>>
    val displayRates = MediatorLiveData<List<ExchangeRateEntity>>()
    val conversionHistory: LiveData<List<ConversionEntity>>
    val errorLogs: LiveData<List<ErrorLogEntity>>
    val historyData: LiveData<List<Entry>> = MutableLiveData()
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        val database = AppDatabase.getDatabase(application)
        val dao = database.currencyDao()
        repository = CurrencyRepository(RetrofitInstance.api, dao)

        allRates = repository.getRatesFromDb().asLiveData()
        conversionHistory = repository.getConversionHistory().asLiveData()
        errorLogs = repository.getErrorLogsFromDb().asLiveData()
        displayRates.addSource(allRates) { rates ->
            recalculateRates(rates)
        }

        NotificationHelper.createNotificationChannel(application)
    }
    fun recalculateRates(rawRates: List<ExchangeRateEntity>?) {
        val rates = rawRates ?: return
        val context = getApplication<Application>()
        val baseCurrency = PrefsManager.getBaseCurrency(context)

        if (baseCurrency == "UAH") {
            displayRates.value = rates
            return
        }

        val baseRateVal = rates.find { it.currencyCode == baseCurrency }?.rate ?: 1.0

        val newRates = rates.map { item ->
            val newRate = item.rate / baseRateVal
            item.copy(rate = newRate)
        }
        displayRates.value = newRates
    }

    fun forceRefreshDisplay() {
        recalculateRates(allRates.value)
    }

    fun refreshData() = viewModelScope.launch {
        _isLoading.value = true
        try {
            repository.refreshRates()
            checkAlerts()
        } catch (e: Exception) {
            e.printStackTrace()
            repository.logError("Error refreshing: ${e.localizedMessage}")
        } finally {
            _isLoading.value = false
        }
    }

    private suspend fun checkAlerts() {
        val rates = repository.getRatesNow()
        val alerts = repository.getAlertsNow()
        alerts.forEach { alert ->
            val currentRate = rates.find { it.currencyCode == alert.currencyCode }?.rate
            if (currentRate != null && alert.isHigher && currentRate >= alert.targetRate) {
                sendNotification(alert.currencyCode, currentRate, alert.targetRate)
                repository.deleteAlert(alert)
            }
        }
    }

    private fun sendNotification(code: String, current: Double, target: Double) {
        NotificationHelper.showNotification(
            getApplication(),
            "Курс $code досяг цілі!",
            "Поточний: $current (Ваша ціль: $target)"
        )
    }

    fun toggleFavorite(rate: ExchangeRateEntity) = viewModelScope.launch {
        repository.toggleFavorite(rate.currencyCode, !rate.isFavorite)
    }

    fun addAlert(code: String, target: Double, isHigher: Boolean) = viewModelScope.launch {
        repository.saveAlert(AlertEntity(currencyCode = code, targetRate = target, isHigher = isHigher))
    }

    fun saveConversion(from: String, to: String, amountFrom: Double, amountTo: Double) = viewModelScope.launch {
        val entry = ConversionEntity(fromCurrency = from, toCurrency = to, amountFrom = amountFrom, amountTo = amountTo)
        repository.saveConversion(entry)
    }

    fun loadHistory(currencyCode: String) = viewModelScope.launch {
        val entries = withContext(Dispatchers.IO) {
            repository.getHistoryForWeek(currencyCode)
        }
        (historyData as MutableLiveData).value = entries
    }
}