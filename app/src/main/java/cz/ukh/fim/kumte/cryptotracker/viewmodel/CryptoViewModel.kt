package cz.ukh.fim.kumte.cryptotracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.ukh.fim.kumte.cryptotracker.model.Coin
import cz.ukh.fim.kumte.cryptotracker.model.PriceAlert
import cz.ukh.fim.kumte.cryptotracker.repository.CryptoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CryptoViewModel : ViewModel() {

    private val repository = CryptoRepository()

    private val _coins = MutableStateFlow<List<Coin>>(emptyList())
    val coins = _coins.asStateFlow()

    private val _czkRate = MutableStateFlow<Double>(21.76)
    val czkRate: StateFlow<Double> = _czkRate.asStateFlow()

    private val _selectedCurrency = MutableStateFlow("USD")
    val selectedCurrency: StateFlow<String> = _selectedCurrency.asStateFlow()

    private val _themeMode = MutableStateFlow(ThemeMode.DARK)
    val themeMode = _themeMode.asStateFlow()

    private val _shakeEnabled = MutableStateFlow(true)
    val shakeEnabled: StateFlow<Boolean> = _shakeEnabled.asStateFlow()

    private val _priceAlerts = MutableStateFlow<List<PriceAlert>>(emptyList())
    val priceAlerts: StateFlow<List<PriceAlert>> = _priceAlerts.asStateFlow()

    private val _notificationInterval = MutableStateFlow(60_000L)
    val notificationInterval: StateFlow<Long> = _notificationInterval

    private val _dataRefreshInterval = MutableStateFlow(120_000L)
    val dataRefreshInterval: StateFlow<Long> = _dataRefreshInterval

    init {
        fetchCoins()
        fetchCzkRate()
    }

    fun fetchCoins(onAlertTriggered: (PriceAlert) -> Unit = {}) {
        viewModelScope.launch {
            try {
                println("Loading data from Coin Gecko API...")
                val coinsList = repository.getCoins()
                withContext(Dispatchers.Main) {
                    _coins.value = coinsList
                    println("Coins updated, size: ${_coins.value.size}")

                    // Check alerts after coins update
                    checkAlerts(onAlertTriggered)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun fetchCzkRate() {
        viewModelScope.launch {
            try {
                println("Loading CZK rate from ČNB...")
                val rate = repository.fetchCzkRate()
                if (rate != null) {
                    _czkRate.value = rate
                } else {
                    println("Failed to fetch CZK rate from ČNB! Using default 21.76...")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setCurrency(currency: String) {
        _selectedCurrency.value = currency
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }

    fun setShakeEnabled(enabled: Boolean) {
        _shakeEnabled.value = enabled
    }

    fun setNotificationInterval(ms: Long) {
        _notificationInterval.value = ms
    }

    fun setDataRefreshInterval(ms: Long) {
        _dataRefreshInterval.value = ms
    }

    fun addOrUpdatePriceAlert(alert: PriceAlert) {
        val updatedList = _priceAlerts.value.toMutableList()
        val index = updatedList.indexOfFirst { it.coinId == alert.coinId }

        if (index != -1) {
            updatedList[index] = alert
        } else {
            updatedList.add(alert)
        }
        _priceAlerts.value = updatedList
    }

    fun removePriceAlert(coinId: String) {
        val updatedList = _priceAlerts.value.filter { it.coinId != coinId }
        _priceAlerts.value = updatedList
    }

    fun checkAlerts(onAlertTriggered: (PriceAlert) -> Unit) {
        val currentPrices = coins.value
        val alerts = _priceAlerts.value

        for (alert in alerts) {
            val matchingCoin = currentPrices.find { it.id == alert.coinId }
            matchingCoin?.let { coin ->
                if (coin.currentPrice <= alert.targetPrice) {
                    onAlertTriggered(alert)
                }
            }
        }
    }
}

enum class ThemeMode {
    LIGHT, DARK
}
