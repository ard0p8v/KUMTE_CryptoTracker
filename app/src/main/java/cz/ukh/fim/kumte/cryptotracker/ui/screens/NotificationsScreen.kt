package cz.ukh.fim.kumte.cryptotracker.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.ukh.fim.kumte.cryptotracker.model.Coin
import cz.ukh.fim.kumte.cryptotracker.model.PriceAlert

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBackClick: () -> Unit,
    priceAlerts: List<PriceAlert>,
    onAlertChange: (PriceAlert) -> Unit,
    onAlertRemove: (String) -> Unit,
    onAlertAdd: (PriceAlert) -> Unit,
    availableCoins: List<Coin>,
    selectedInterval: Long,
    onIntervalChange: (Long) -> Unit,
    dataRefreshInterval: Long,
    onDataRefreshIntervalChange: (Long) -> Unit
) {
    var selectedCoinId by remember { mutableStateOf("") }
    var targetPriceInput by remember { mutableStateOf("") }
    var selectedInterval by remember { mutableLongStateOf(selectedInterval) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crypto Tracker Notifications", color = MaterialTheme.colorScheme.primary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Get notifications interval",
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.weight(1f)) // Odsune tlačítko doprava

                val options = listOf(
                    15_000L to "15 seconds",
                    30_000L to "30 seconds",
                    60_000L to "1 minute",
                    300_000L to "5 minutes"
                )

                var intervalDropdownExpanded by remember { mutableStateOf(false) }

                Box {
                    OutlinedButton(onClick = { intervalDropdownExpanded = true }) {
                        Text(
                            text = options.firstOrNull { it.first == selectedInterval }?.second ?: "Select",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    DropdownMenu(
                        expanded = intervalDropdownExpanded,
                        onDismissRequest = { intervalDropdownExpanded = false }
                    ) {
                        options.forEach { (ms, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    selectedInterval = ms
                                    onIntervalChange(ms)
                                    intervalDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Data refresh interval",
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.weight(1f))

                val options = listOf(
                    120_000L to "2 minutes",
                    300_000L to "5 minutes",
                    600_000L to "10 minutes",
                    1800_000L to "30 minutes"
                )

                var dropdownExpanded by remember { mutableStateOf(false) }
                var localInterval by remember { mutableStateOf(dataRefreshInterval) }

                Box {
                    OutlinedButton(onClick = { dropdownExpanded = true }) {
                        Text(
                            text = options.firstOrNull { it.first == localInterval }?.second ?: "Select",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        options.forEach { (ms, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    localInterval = ms
                                    onDataRefreshIntervalChange(ms)
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Text(
                text = "Price Alerts",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    var expanded by remember { mutableStateOf(false) }

                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { expanded = true }
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (selectedCoinId.isNotEmpty()) selectedCoinId else "Select Crypto",
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Expand",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            availableCoins.forEach { coin ->
                                DropdownMenuItem(
                                    text = { Text(coin.name) },
                                    onClick = {
                                        selectedCoinId = coin.id
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    TextField(
                        value = targetPriceInput,
                        onValueChange = { targetPriceInput = it },
                        label = { Text("Target Price", fontSize = 13.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp),
                        modifier = Modifier.width(120.dp).height(50.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(onClick = {
                        val price = targetPriceInput.toDoubleOrNull()
                        if (selectedCoinId.isNotEmpty() && price != null) {
                            onAlertAdd(PriceAlert(selectedCoinId, selectedCoinId, price))
                            targetPriceInput = ""
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Alert",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    items = priceAlerts,
                    key = { it.coinId }
                ) { alert ->
                    val inputValue = rememberSaveable(alert.coinId) {
                        mutableStateOf(alert.targetPrice.toString())
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(alert.coinName, color = MaterialTheme.colorScheme.onBackground)
                        TextField(
                            value = inputValue.value,
                            onValueChange = { newValue ->
                                inputValue.value = newValue
                                newValue.toDoubleOrNull()?.let { price ->
                                    onAlertChange(alert.copy(targetPrice = price))
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = { Text("Target Price") },
                            modifier = Modifier.width(150.dp)
                        )
                        IconButton(onClick = { onAlertRemove(alert.coinId) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Remove Alert")
                        }
                    }
                }
            }
        }
    }
}
