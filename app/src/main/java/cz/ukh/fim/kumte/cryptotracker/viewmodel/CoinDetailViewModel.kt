package cz.ukh.fim.kumte.cryptotracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.ukh.fim.kumte.cryptotracker.model.CoinDetail
import cz.ukh.fim.kumte.cryptotracker.model.MarketChartResponse
import cz.ukh.fim.kumte.cryptotracker.repository.CryptoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CoinDetailViewModel(private val repository: CryptoRepository) : ViewModel() {

    private val _coinDetail = MutableStateFlow<CoinDetail?>(null)
    val coinDetail: StateFlow<CoinDetail?> = _coinDetail.asStateFlow()
    private val _marketChart = MutableStateFlow<MarketChartResponse?>(null)
    val marketChart: StateFlow<MarketChartResponse?> = _marketChart.asStateFlow()

    fun fetchCoinDetail(id: String) {
        viewModelScope.launch {
            try {
                val detail = repository.getCoinDetail(id)
                _coinDetail.value = detail
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchMarketChart(coinId: String, days: Int = 7) {
        viewModelScope.launch {
            try {
                val chartData = repository.getMarketChart(coinId, days)
                _marketChart.value = chartData
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}