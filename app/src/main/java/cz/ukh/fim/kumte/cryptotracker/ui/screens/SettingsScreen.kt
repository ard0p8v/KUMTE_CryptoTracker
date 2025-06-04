package cz.ukh.fim.kumte.cryptotracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.ukh.fim.kumte.cryptotracker.viewmodel.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    shakeEnabled: Boolean,
    onShakeChange: (Boolean) -> Unit,
    selectedCurrency: String,
    onCurrencyChange: (String) -> Unit,
    selectedThemeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crypto Tracker Settings", color = MaterialTheme.colorScheme.primary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
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
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Shake to refresh crypto data", color = MaterialTheme.colorScheme.onBackground)
                Switch(
                    checked = shakeEnabled,
                    onCheckedChange = onShakeChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurface,
                        uncheckedTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    )
                )
            }

            Text(
                text = "Currency selection",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedCurrency.equals("USD", ignoreCase = true),
                    onClick = { onCurrencyChange("USD") },
                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                )
                Text(text = "USD", color = MaterialTheme.colorScheme.onBackground)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedCurrency.equals("CZK", ignoreCase = true),
                    onClick = { onCurrencyChange("CZK") },
                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                )
                Text(text = "CZK", color = MaterialTheme.colorScheme.onBackground)
            }

            Text(
                text = "Mode selection",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedThemeMode == ThemeMode.LIGHT,
                    onClick = { onThemeModeChange(ThemeMode.LIGHT) },
                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                )
                Text("Light", color = MaterialTheme.colorScheme.onBackground)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedThemeMode == ThemeMode.DARK,
                    onClick = { onThemeModeChange(ThemeMode.DARK) },
                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                )
                Text("Dark", color = MaterialTheme.colorScheme.onBackground)
            }
        }
    }
}

