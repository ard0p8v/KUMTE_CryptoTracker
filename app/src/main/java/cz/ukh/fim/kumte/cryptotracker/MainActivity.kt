package cz.ukh.fim.kumte.cryptotracker

import ShakeDetector
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cz.ukh.fim.kumte.cryptotracker.ui.screens.HomeScreen
import cz.ukh.fim.kumte.cryptotracker.ui.theme.KUMTE_CryptoTrackerTheme
import cz.ukh.fim.kumte.cryptotracker.viewmodel.CryptoViewModel
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.activity.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cz.ukh.fim.kumte.cryptotracker.repository.CryptoRepository
import cz.ukh.fim.kumte.cryptotracker.ui.screens.CoinDetailScreen
import cz.ukh.fim.kumte.cryptotracker.viewmodel.CoinDetailViewModel
import cz.ukh.fim.kumte.cryptotracker.viewmodel.CoinDetailViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var shakeDetector: ShakeDetector
    private val viewModel: CryptoViewModel by viewModels()

    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // initializing shakeDetector outside of compose
        shakeDetector = ShakeDetector(this) {
            println("Shake detected (simulation)..")
            viewModel.fetchCoins()
        }

        lifecycleScope.launch {
            delay(60_000) // 1 minute
            println("Simulated shake after 1 minute..")
            shakeDetector.triggerShake()
        }

        setContent {
            KUMTE_CryptoTrackerTheme {
                val navController = rememberNavController()
                val repository = CryptoRepository()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF1A1A1A)
                ) {
                    NavHost(navController = navController, startDestination = "home") {

                        composable("home") {
                            HomeScreen(
                                viewModel = viewModel,
                                onRefresh = { viewModel.fetchCoins() },
                                onCoinClick = { coinId ->
                                    navController.navigate("coinDetail/$coinId")
                                }
                            )
                        }

                        composable("coinDetail/{coinId}") { backStackEntry ->
                            val coinId = backStackEntry.arguments?.getString("coinId") ?: ""
                            val coinDetailViewModel: CoinDetailViewModel = viewModel(
                                factory = CoinDetailViewModelFactory(repository)
                            )
                            CoinDetailScreen(
                                coinId = coinId,
                                viewModel = coinDetailViewModel,
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        shakeDetector.start()
    }

    override fun onPause() {
        super.onPause()
        shakeDetector.stop()
    }
}

@Composable
fun LogoView() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo aplikace",
            modifier = Modifier
                .width(250.dp)
                .height(100.dp)
        )
    }
}