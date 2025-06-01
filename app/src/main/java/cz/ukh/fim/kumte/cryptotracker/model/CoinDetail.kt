package cz.ukh.fim.kumte.cryptotracker.model

import com.google.gson.annotations.SerializedName

data class CoinDetail(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("symbol") val symbol: String,
    @SerializedName("description") val description: Map<String, String>,
    @SerializedName("market_data") val marketData: MarketData
)