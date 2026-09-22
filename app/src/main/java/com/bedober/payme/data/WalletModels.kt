package com.bedober.payme.data

import java.util.UUID

data class Wallet(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val currency: String,
    val balance: Double,
    val isCrypto: Boolean = false,
    val color: Long = 0xFF1976D2
)

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val type: TransactionType,
    val title: String,
    val amount: Double,
    val currency: String,
    val counterparty: String,
    val status: TransactionStatus,
    val createdAt: Long = System.currentTimeMillis()
)

enum class TransactionType { SEND, RECEIVE, EXCHANGE, TOP_UP }
enum class TransactionStatus { PENDING, COMPLETED, FAILED }

data class ExchangeQuote(
    val fromCurrency: String,
    val toCurrency: String,
    val fromAmount: Double,
    val toAmount: Double,
    val rate: Double,
    val fee: Double = 0.0
)

val sampleWallets = listOf(
    Wallet(name = "Main Wallet", currency = "USD", balance = 18340.25, isCrypto = false, color = 0xFF2E7D32),
    Wallet(name = "Euros", currency = "EUR", balance = 8450.0, isCrypto = false, color = 0xFF1565C0),
    Wallet(name = "Crypto Vault", currency = "BTC", balance = 1.347, isCrypto = true, color = 0xFFF9A825),
    Wallet(name = "Stablecoin", currency = "USDT", balance = 2400.0, isCrypto = true, color = 0xFF26A69A)
)
