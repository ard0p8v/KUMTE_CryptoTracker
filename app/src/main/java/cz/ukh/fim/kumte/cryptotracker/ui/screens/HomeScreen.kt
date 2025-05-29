package cz.ukh.fim.kumte.cryptotracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.ukh.fim.kumte.cryptotracker.viewmodel.CryptoViewModel

@Composable
fun HomeScreen() {
    val viewModel: CryptoViewModel = viewModel()
    val coins = viewModel.coins.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        items(coins.value) { coin ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = coin.name, style = MaterialTheme.typography.titleMedium)
                    Text(text = "Cena: ${coin.currentPrice} USD")
                    Text(text = "24h změna: ${String.format("%.2f", coin.priceChangePercentage24h)} %")
                }
            }
        }
    }
}