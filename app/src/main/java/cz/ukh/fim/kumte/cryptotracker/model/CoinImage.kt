package cz.ukh.fim.kumte.cryptotracker.model

import com.google.gson.annotations.SerializedName

data class CoinImage(
    @SerializedName("thumb") val thumb: String,
    @SerializedName("small") val small: String,
    @SerializedName("large") val large: String
)