package cz.ukh.fim.kumte.cryptotracker.model

data class PriceAlert(
    val coinId: String,
    val coinName: String,
    val targetPrice: Double,
    val enabled: Boolean = true
)