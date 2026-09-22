package com.bedober.payme.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WalletRepository(private val db: PaymeDatabase) {
    fun wallets(): Flow<List<Wallet>> = db.walletDao().observeWallets().map { it.map { entity -> entity.toModel() } }
    fun transactions(): Flow<List<Transaction>> = db.transactionDao().observeTransactions().map { it.map { entity -> entity.toModel() } }

    suspend fun seedIfEmpty() {
        val wallets = db.walletDao().observeWallets() // no-op placeholder to avoid using flow directly here
        val current = wallets.firstOrNull()
        if (current.isNullOrEmpty()) {
            sampleWallets.forEach { db.walletDao().upsertWallet(it.toEntity()) }
            listOf(
                Transaction(
                    type = TransactionType.TOP_UP,
                    title = "Wallet funding",
                    amount = 1500.0,
                    currency = "USD",
                    counterparty = "Bank transfer",
                    status = TransactionStatus.COMPLETED
                ),
                Transaction(
                    type = TransactionType.SEND,
                    title = "Transfer to Sarah",
                    amount = 250.0,
                    currency = "USD",
                    counterparty = "Sarah",
                    status = TransactionStatus.COMPLETED
                ),
                Transaction(
                    type = TransactionType.EXCHANGE,
                    title = "BTC to USD",
                    amount = 0.25,
                    currency = "BTC",
                    counterparty = "Exchange",
                    status = TransactionStatus.PENDING
                )
            ).forEach { db.transactionDao().insert(it.toEntity()) }
        }
    }

    suspend fun sendMoney(fromCurrency: String, toAccount: String, amount: Double): Boolean {
        val targetWallet = db.walletDao().observeWallets().firstOrNull()?.firstOrNull { it.currency == fromCurrency }
        if (targetWallet == null || amount <= 0.0) return false
        val newBalance = targetWallet.balance - amount
        db.walletDao().updateBalance(targetWallet.id, newBalance)
        db.transactionDao().insert(
            Transaction(
                type = TransactionType.SEND,
                title = "Transfer to $toAccount",
                amount = amount,
                currency = fromCurrency,
                counterparty = toAccount,
                status = TransactionStatus.COMPLETED
            ).toEntity()
        )
        return true
    }

    fun generateQuote(fromCurrency: String, toCurrency: String, amount: Double): ExchangeQuote {
        val rateMap = mapOf(
            "USD" to mapOf("EUR" to 0.92, "BTC" to 0.000021, "USDT" to 1.0),
            "EUR" to mapOf("USD" to 1.09, "BTC" to 0.000023),
            "BTC" to mapOf("USD" to 48000.0, "EUR" to 42000.0, "USDT" to 47000.0),
            "USDT" to mapOf("USD" to 1.0, "EUR" to 0.92)
        )
        val rate = rateMap[fromCurrency]?.get(toCurrency) ?: 1.0
        val converted = amount * rate
        return ExchangeQuote(fromCurrency, toCurrency, amount, converted, rate, 0.0)
    }
}
