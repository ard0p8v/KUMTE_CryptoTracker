package cz.ukh.fim.kumte.cryptotracker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.ukh.fim.kumte.cryptotracker.LogoView
import cz.ukh.fim.kumte.cryptotracker.model.Coin
import cz.ukh.fim.kumte.cryptotracker.util.formatNumberWithSpace
import cz.ukh.fim.kumte.cryptotracker.viewmodel.CryptoViewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: CryptoViewModel,
    onRefresh: () -> Unit,
    onCoinClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    selectedCurrency: String,
    onCurrencyChange: (String) -> Unit,
    isDarkTheme: Boolean
) {
    val coins = viewModel.coins.collectAsState()
    val czkRate = viewModel.czkRate.collectAsState()

    LaunchedEffect(coins.value) {
        println("LaunchedEffect - coins changed, size: ${coins.value.size}")
    }

    println("Collected coins: ${coins.value.size}")
    println("Actual CZK rate from ČNB: ${czkRate}")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { LogoView(isDarkTheme) },
                actions = {
                    IconButton(onClick = { viewModel.fetchCoins() }) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Refresh",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(25.dp)
                        )
                    }
                    IconButton(onClick = { onNotificationsClick() }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    IconButton(onClick = { onSettingsClick() }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
        ) {
            Text(
                text = "TOP 30 cryptos today",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp),  // Přidáno dolní odsazení
                textAlign = TextAlign.Center
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                items(coins.value) { coin ->
                    CoinItem(
                        coin = coin,
                        selectedCurrency = selectedCurrency,
                        czkRate = czkRate.value,
                        onClick = { onCoinClick(coin.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun CoinItem(
    coin: Coin,
    selectedCurrency: String,
    czkRate: Double,
    onClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable { onClick(coin.id) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = coin.symbol.uppercase(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            val price = if (selectedCurrency.lowercase() == "czk") {
                coin.currentPrice * czkRate
            } else {
                coin.currentPrice
            }

            Text(
                text = "${formatNumberWithSpace(price)} ${selectedCurrency.uppercase()}",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
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
