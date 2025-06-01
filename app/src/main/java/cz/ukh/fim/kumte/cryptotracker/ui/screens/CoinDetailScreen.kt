package cz.ukh.fim.kumte.cryptotracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import cz.ukh.fim.kumte.cryptotracker.repository.CryptoRepository
import cz.ukh.fim.kumte.cryptotracker.util.formatNumberWithSpace
import cz.ukh.fim.kumte.cryptotracker.viewmodel.CoinDetailViewModel
import cz.ukh.fim.kumte.cryptotracker.viewmodel.CoinDetailViewModelFactory
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinDetailScreen(
    coinId: String,
    viewModel: CoinDetailViewModel = viewModel(factory = CoinDetailViewModelFactory(CryptoRepository())),
    navController: NavController
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
                title = { Text(text = coinDetail?.name ?: "Detail", color = Color(0xFF00D1B2)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2A2A2A)
                )
            )
        },
        containerColor = Color(0xFF1A1A1A)
    ) { padding ->
        coinDetail?.let { detail ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(text = detail.symbol.uppercase(), color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                Text(text = detail.name, color = Color(0xFF00D1B2), style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = detail.description["en"] ?: "No description available.", color = Color.White)

                Spacer(modifier = Modifier.height(24.dp))

                marketChart?.let { chartData ->
                    val entries = chartData.prices.mapIndexed { index, price ->
                        FloatEntry(x = index.toFloat(), y = price[1].toFloat())
                    }
                    val chartEntryModelProducer = ChartEntryModelProducer(entries)

                    Text(
                        text = "Price development in the last 7 days",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF00D1B2),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Chart(
                        chart = lineChart(),
                        chartModelProducer = chartEntryModelProducer,
                        startAxis = rememberStartAxis(),
                        bottomAxis = rememberBottomAxis(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Current Price: ${formatNumberWithSpace(detail.marketData.currentPrice["usd"] ?: 0.0)} USD",
                    color = Color(0xFF00D1B2)
                )
                Text(
                    text = "Market Cap: ${formatNumberWithSpace(detail.marketData.marketCap["usd"] ?: 0.0)} USD",
                    color = Color(0xFF00D1B2)
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

