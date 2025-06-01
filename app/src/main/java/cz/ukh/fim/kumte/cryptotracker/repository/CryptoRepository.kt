package cz.ukh.fim.kumte.cryptotracker.repository

import cz.ukh.fim.kumte.cryptotracker.api.RetrofitInstance
import cz.ukh.fim.kumte.cryptotracker.model.Coin
import cz.ukh.fim.kumte.cryptotracker.model.CoinDetail
import cz.ukh.fim.kumte.cryptotracker.model.MarketChartResponse

class CryptoRepository {
    suspend fun getCoins(): List<Coin> {
        return RetrofitInstance.api.getCoins()
    }

    suspend fun getCoinDetail(id: String): CoinDetail {
        return RetrofitInstance.api.getCoinDetail(id)
    }

    suspend fun getMarketChart(coinId: String, days: Int = 7): MarketChartResponse {
        return RetrofitInstance.api.getMarketChart(id = coinId, days = days)
    }
}