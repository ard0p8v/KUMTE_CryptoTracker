package cz.ukh.fim.kumte.cryptotracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import cz.ukh.fim.kumte.cryptotracker.repository.CryptoRepository
import cz.ukh.fim.kumte.cryptotracker.util.formatNumberWithSpace
import cz.ukh.fim.kumte.cryptotracker.viewmodel.CoinDetailViewModel
import cz.ukh.fim.kumte.cryptotracker.viewmodel.CoinDetailViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinDetailScreen(
    coinId: String,
    viewModel: CoinDetailViewModel = viewModel(factory = CoinDetailViewModelFactory(CryptoRepository())),
    navController: NavController,
    selectedCurrency: String,
    czkRate: Double
) {
    val coinDetail = viewModel.coinDetail.collectAsState().value
    val marketChart = viewModel.marketChart.collectAsState().value

    LaunchedEffect(coinId) {
        viewModel.fetchCoinDetail(coinId)
        viewModel.fetchMarketChart(coinId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = coinDetail?.name ?: "Detail", color = MaterialTheme.colorScheme.primary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        coinDetail?.let { detail ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(text = detail.symbol.uppercase(), color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.bodySmall)
                Text(text = detail.name, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = detail.description["en"] ?: "No description available.", color = MaterialTheme.colorScheme.onBackground)

                Spacer(modifier = Modifier.height(24.dp))

                marketChart?.let { chartData ->
                    Text(
                        text = "Price development in the last 7 days",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    val minY = (chartData.prices.minOf { it[1] } * 0.95).toFloat()
                    val entries = buildList {
                        add(entryOf(x = 0f, y = minY))
                        addAll(chartData.prices.mapIndexed { index, price ->
                            val adjustedPrice = if (selectedCurrency.lowercase() == "czk") {
                                price[1] * czkRate
                            } else {
                                price[1]
                            }
                            entryOf(x = index.toFloat(), y = adjustedPrice.toFloat())
                        })
                    }

                    val chartEntryModelProducer = ChartEntryModelProducer(entries)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        Chart(
                            chart = lineChart(),
                            chartModelProducer = chartEntryModelProducer,
                            startAxis = rememberStartAxis(),
                            bottomAxis = rememberBottomAxis()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val currentPrice = if (selectedCurrency.lowercase() == "czk") {
                    (detail.marketData.currentPrice["usd"] ?: 0.0) * czkRate
                } else {
                    detail.marketData.currentPrice["usd"] ?: 0.0
                }

                val marketCap = if (selectedCurrency.lowercase() == "czk") {
                    (detail.marketData.marketCap["usd"] ?: 0.0) * czkRate
                } else {
                    detail.marketData.marketCap["usd"] ?: 0.0
                }

                val currencySuffix = selectedCurrency.uppercase()

                Text(
                    text = "Current Price: ${formatNumberWithSpace(currentPrice)} $currencySuffix",
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Market Cap: ${formatNumberWithSpace(marketCap)} $currencySuffix",
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "24h Change: ${String.format("%.2f", detail.marketData.priceChangePercentage24h)} %",
                    color = if (detail.marketData.priceChangePercentage24h >= 0) Color.Green else Color.Red
                )
            }
        } ?: Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}



