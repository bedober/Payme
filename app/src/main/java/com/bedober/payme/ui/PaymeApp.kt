package com.bedober.payme.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bedober.payme.data.*
import kotlinx.coroutines.launch

@Composable
fun PaymeApp(vm: PaymeViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payme") },
                actions = {
                    TextButton(onClick = {}) { Text("Profile") }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                listOf("Home", "Send", "Exchange", "Activity").forEachIndexed { index, label ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            when (index) {
                                0 -> Icon(Icons.Default.AccountBalanceWallet, null)
                                1 -> Icon(Icons.Default.ArrowForward, null)
                                2 -> Icon(Icons.Default.SwapHoriz, null)
                                else -> Icon(Icons.Default.Money, null)
                            }
                        },
                        label = { Text(label) }
                    )
                }
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> DashboardScreen(vm)
                1 -> SendScreen(vm)
                2 -> ExchangeScreen(vm)
                3 -> ActivityScreen(vm)
            }
        }
    }
}

@Composable
private fun DashboardScreen(vm: PaymeViewModel) {
    val wallets by vm.wallets.collectAsState()
    val total = wallets.sumOf { it.balance }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp)) {
                    Text("Total balance", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Text("$${String.format("%.2f", total)}", style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AssistChip(onClick = {}, label = { Text("Top up") })
                        AssistChip(onClick = {}, label = { Text("Request") })
                    }
                }
            }
        }

        item {
            Text("Wallets", style = MaterialTheme.typography.titleLarge)
        }

        items(wallets) { wallet ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(wallet.color), shape = MaterialTheme.shapes.small),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(wallet.currency.take(1), color = Color.White)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(wallet.name, style = MaterialTheme.typography.titleMedium)
                        Text(wallet.currency, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("${wallet.currency} ${String.format("%.2f", wallet.balance)}", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
private fun SendScreen(vm: PaymeViewModel) {
    var recipient by remember { mutableStateOf("Sarah Johnson") }
    var amount by remember { mutableStateOf("250") }
    var selectedCurrency by remember { mutableStateOf("USD") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Send money", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(value = recipient, onValueChange = { recipient = it }, label = { Text("Recipient") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = selectedCurrency, onValueChange = { selectedCurrency = it }, label = { Text("Currency") }, modifier = Modifier.fillMaxWidth())
        Button(
            onClick = {
                vm.sendMoney(selectedCurrency, recipient, amount.toDoubleOrNull() ?: 0.0)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send now")
        }
    }
}

@Composable
private fun ExchangeScreen(vm: PaymeViewModel) {
    var fromCurrency by remember { mutableStateOf("USD") }
    var toCurrency by remember { mutableStateOf("BTC") }
    var amount by remember { mutableStateOf("100") }
    val quote by vm.quote.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Crypto & FX exchange", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(value = fromCurrency, onValueChange = { fromCurrency = it }, label = { Text("From") })
        OutlinedTextField(value = toCurrency, onValueChange = { toCurrency = it }, label = { Text("To") })
        OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount") })
        Button(
            onClick = {
                vm.calculateQuote(fromCurrency, toCurrency, amount.toDoubleOrNull() ?: 0.0)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Get quote")
        }

        if (quote != null) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Estimated conversion", style = MaterialTheme.typography.titleMedium)
                    Text("${quote.fromAmount} ${quote.fromCurrency} = ${quote.toAmount} ${quote.toCurrency}")
                    Text("Rate: ${quote.rate}")
                    Text("Fee: ${quote.fee}")
                }
            }
        }
    }
}

@Composable
private fun ActivityScreen(vm: PaymeViewModel) {
    val transactions by vm.transactions.collectAsState()

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Recent activity", style = MaterialTheme.typography.headlineSmall)
        }
        items(transactions) { tx ->
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(tx.title, style = MaterialTheme.typography.titleMedium)
                        Text(tx.counterparty, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("${tx.currency} ${String.format("%.2f", tx.amount)}", style = MaterialTheme.typography.titleMedium)
                        Text(tx.status.name, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
