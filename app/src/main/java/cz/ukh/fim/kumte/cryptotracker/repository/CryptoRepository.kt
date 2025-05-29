package cz.ukh.fim.kumte.cryptotracker.repository

import cz.ukh.fim.kumte.cryptotracker.api.RetrofitInstance
import cz.ukh.fim.kumte.cryptotracker.model.Coin

class CryptoRepository {
    suspend fun getCoins(): List<Coin> {
        return RetrofitInstance.api.getCoins()
    }
}