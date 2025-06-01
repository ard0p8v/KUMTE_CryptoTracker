package cz.ukh.fim.kumte.cryptotracker.model


data class MarketChartResponse(
    val prices: List<List<Double>>
)