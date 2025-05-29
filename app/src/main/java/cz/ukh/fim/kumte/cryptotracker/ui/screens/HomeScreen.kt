package cz.ukh.fim.kumte.cryptotracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.ukh.fim.kumte.cryptotracker.LogoView
import cz.ukh.fim.kumte.cryptotracker.model.Coin
import cz.ukh.fim.kumte.cryptotracker.util.formatNumberWithSpace
import cz.ukh.fim.kumte.cryptotracker.viewmodel.CryptoViewModel
import coil.compose.AsyncImage

@Composable
fun HomeScreen() {
    val viewModel: CryptoViewModel = viewModel()
    val coins = viewModel.coins.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LogoView()

        Button(
            onClick = { viewModel.fetchCoins() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00D1B2),
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("REFRESH DATA")
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            items(coins.value) { coin ->
                CoinItem(coin)
            }
        }
    }
}

@Composable
fun CoinItem(coin: Coin) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = coin.image,
                    contentDescription = "${coin.name} logo",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 8.dp)
                )
                Column {
                    Text(
                        text = coin.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF00D1B2)
                    )
                    Text(
                        text = coin.symbol.uppercase(),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )
                }
            }

            Text(
                text = "${formatNumberWithSpace(coin.currentPrice)} USD",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF00D1B2)
            )
        }

        Text(
            text = "24h změna: ${String.format("%.2f", coin.priceChangePercentage24h)} %",
            color = if (coin.priceChangePercentage24h >= 0) Color.Green else Color.Red,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 15.dp, bottom = 10.dp)
        )
    }
}
