package cz.ukh.fim.kumte.cryptotracker

import ShakeDetector
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.compose.runtime.collectAsState
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cz.ukh.fim.kumte.cryptotracker.repository.CryptoRepository
import cz.ukh.fim.kumte.cryptotracker.ui.screens.CoinDetailScreen
import cz.ukh.fim.kumte.cryptotracker.ui.screens.NotificationsScreen
import cz.ukh.fim.kumte.cryptotracker.ui.screens.SettingsScreen
import cz.ukh.fim.kumte.cryptotracker.viewmodel.CoinDetailViewModel
import cz.ukh.fim.kumte.cryptotracker.viewmodel.CoinDetailViewModelFactory
import cz.ukh.fim.kumte.cryptotracker.viewmodel.ThemeMode

class MainActivity : ComponentActivity() {

    private lateinit var shakeDetector: ShakeDetector
    private val viewModel: CryptoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    999
                )
            }
        }

        shakeDetector = ShakeDetector(this) {
            println("Shake detected (simulation)..")
            viewModel.fetchCoins()
            viewModel.checkAlerts { alert ->
                println("${alert.coinName} překročil cenu ${alert.targetPrice}!")
                // Zde můžeš dát Toast, Notification, AlertDialog...
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.shakeEnabled.collect { enabled ->
                    if (enabled) shakeDetector.start() else shakeDetector.stop()
                }
            }
        }

        lifecycleScope.launch {
            delay(60_000)
            println("Simulated shake after 1 minute..")
            shakeDetector.triggerShake()
            viewModel.checkAlerts { alert ->
                println("${alert.coinName} exceeded the price ${alert.targetPrice}!")
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (true) {
                    viewModel.checkAlerts { alert ->
                        showPriceAlertNotification(
                            context = this@MainActivity,
                            title = "Price Alert: ${alert.coinName}",
                            message = "Price reached ${alert.targetPrice}"
                        )
                    }
                    delay(60_000) // Opakuj každou minutu
                }
            }
        }

        setContent {
            val selectedThemeMode = viewModel.themeMode.collectAsState().value
            val isDarkTheme = selectedThemeMode == ThemeMode.DARK

            KUMTE_CryptoTrackerTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                val repository = CryptoRepository()
                val selectedCurrency = viewModel.selectedCurrency.collectAsState().value

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF1A1A1A)
                ) {
                    NavHost(navController = navController, startDestination = "home") {

                        composable("home") {
                            HomeScreen(
                                viewModel = viewModel,
                                onRefresh = { viewModel.fetchCoins() },
                                onCoinClick = { coinId -> navController.navigate("coinDetail/$coinId") },
                                onSettingsClick = { navController.navigate("settings") },
                                onNotificationsClick = { navController.navigate("notifications") },
                                selectedCurrency = selectedCurrency,
                                onCurrencyChange = { viewModel.setCurrency(it) },
                                isDarkTheme = isDarkTheme
                            )
                        }

                        composable("notifications") {
                            NotificationsScreen(
                                onBackClick = { navController.popBackStack() },
                                shakeEnabled = viewModel.shakeEnabled.collectAsState().value,
                                onShakeChange = { viewModel.setShakeEnabled(it) },
                                priceAlerts = viewModel.priceAlerts.collectAsState().value,
                                onAlertChange = { viewModel.addOrUpdatePriceAlert(it) },
                                onAlertRemove = { viewModel.removePriceAlert(it) },
                                onAlertAdd = { viewModel.addOrUpdatePriceAlert(it) },
                                availableCoins = viewModel.coins.collectAsState().value
                            )
                        }

                        composable("settings") {
                            SettingsScreen(
                                selectedCurrency = selectedCurrency,
                                onCurrencyChange = { viewModel.setCurrency(it) },
                                selectedThemeMode = selectedThemeMode,
                                onThemeModeChange = { viewModel.setThemeMode(it) },
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        composable("coinDetail/{coinId}") { backStackEntry ->
                            val coinId = backStackEntry.arguments?.getString("coinId") ?: ""
                            val coinDetailViewModel: CoinDetailViewModel = viewModel(
                                factory = CoinDetailViewModelFactory(repository)
                            )
                            val czkRate = viewModel.czkRate.collectAsState().value

                            CoinDetailScreen(
                                coinId = coinId,
                                viewModel = coinDetailViewModel,
                                navController = navController,
                                selectedCurrency = selectedCurrency,
                                czkRate = czkRate
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }
}

@Composable
fun LogoView(isDarkTheme: Boolean) {
    val logoRes = if (isDarkTheme) R.drawable.logo_dark else R.drawable.logo_light

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 2.dp, top = 2.dp, bottom = 2.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Image(
            painter = painterResource(id = logoRes),
            contentDescription = "Application logo",
            modifier = Modifier
                .width(130.dp)
                .height(50.dp)
        )
    }
}

private fun showPriceAlertNotification(context: Context, title: String, message: String) {
    val channelId = "price_alert_channel"
    val notificationId = 1

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Price Alerts",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for crypto price alerts"
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .build()

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
        NotificationManagerCompat.from(context).notify(notificationId, notification)
    } else {
        println("Notification is not allowed.")
    }
}