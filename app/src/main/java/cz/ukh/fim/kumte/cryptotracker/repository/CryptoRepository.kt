package cz.ukh.fim.kumte.cryptotracker.repository

import cz.ukh.fim.kumte.cryptotracker.api.RetrofitInstance
import cz.ukh.fim.kumte.cryptotracker.model.Coin
import cz.ukh.fim.kumte.cryptotracker.model.CoinDetail
import cz.ukh.fim.kumte.cryptotracker.model.MarketChartResponse
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

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

    suspend fun fetchCzkRate(): Double? {
        val client = HttpClient(CIO)
        val response = client.get("https://www.cnb.cz/en/financial-markets/foreign-exchange-market/exchange-rate-fixing/daily.txt")
        val lines = response.bodyAsText().lines()

        val usdLine = lines.find { it.contains("|USD|") }
        return usdLine
            ?.split("\\|".toRegex())
            ?.getOrNull(4)
            ?.replace(",", ".")
            ?.toDoubleOrNull()
    }
}