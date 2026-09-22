package com.bedober.payme.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class WalletRepository(private val db: PaymeDatabase) {
    fun wallets(): Flow<List<Wallet>> = db.walletDao().observeWallets().map { it.map { entity -> entity.toModel() } }
    fun transactions(): Flow<List<Transaction>> = db.transactionDao().observeTransactions().map { it.map { entity -> entity.toModel() } }

    suspend fun seedIfEmpty() {
        val current = db.walletDao().observeWallets().first()
        if (current.isEmpty()) {
            sampleWallets.forEach { db.walletDao().upsertWallet(it.toEntity()) }
            listOf(
                Transaction(TransactionType.TOP_UP, "Wallet funding", 1500.0, "USD", "Bank transfer", TransactionStatus.COMPLETED),
                Transaction(TransactionType.SEND, "Transfer to Sarah", 250.0, "USD", "Sarah", TransactionStatus.COMPLETED),
                Transaction(TransactionType.EXCHANGE, "BTC to USD", 0.25, "BTC", "Exchange", TransactionStatus.PENDING)
            ).forEach { db.transactionDao().insert(it.toEntity()) }
        }
    }

    suspend fun sendMoney(fromCurrency: String, toAccount: String, amount: Double): Boolean {
        val targetWallet = db.walletDao().observeWallets().first().firstOrNull { it.currency == fromCurrency }
        if (targetWallet == null || amount <= 0.0 || targetWallet.balance < amount) return false
        db.walletDao().updateBalance(targetWallet.id, targetWallet.balance - amount)
        db.transactionDao().insert(Transaction(TransactionType.SEND, "Transfer to $toAccount", amount, fromCurrency, toAccount, TransactionStatus.COMPLETED).toEntity())
        return true
    }

    fun generateQuote(fromCurrency: String, toCurrency: String, amount: Double): ExchangeQuote {
        val rate = mapOf(
            "USD" to mapOf("EUR" to 0.92, "BTC" to 0.000021, "USDT" to 1.0),
            "EUR" to mapOf("USD" to 1.09, "BTC" to 0.000023),
            "BTC" to mapOf("USD" to 48000.0, "EUR" to 42000.0, "USDT" to 47000.0),
            "USDT" to mapOf("USD" to 1.0, "EUR" to 0.92)
        )[fromCurrency]?.get(toCurrency) ?: 1.0
        return ExchangeQuote(fromCurrency, toCurrency, amount, amount * rate, rate, 0.0)
    }
}
