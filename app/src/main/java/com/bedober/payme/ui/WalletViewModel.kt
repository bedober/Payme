package com.bedober.payme.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bedober.payme.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PaymeViewModel(application: Application) : AndroidViewModel(application) {
    private val db = PaymeDatabase.get(application)
    private val repository = WalletRepository(db)
    private val _wallets = MutableStateFlow<List<Wallet>>(emptyList())
    val wallets: StateFlow<List<Wallet>> = _wallets.asStateFlow()
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()
    private val _quote = MutableStateFlow<ExchangeQuote?>(null)
    val quote: StateFlow<ExchangeQuote?> = _quote.asStateFlow()

    init {
        viewModelScope.launch { repository.seedIfEmpty() }
        viewModelScope.launch { repository.wallets().collect { _wallets.value = it } }
        viewModelScope.launch { repository.transactions().collect { _transactions.value = it } }
    }

    fun sendMoney(currency: String, recipient: String, amount: Double) {
        viewModelScope.launch {
            val sent = repository.sendMoney(currency, recipient, amount)
            if (!sent) _transactions.value = listOf(Transaction(TransactionType.SEND, "Failed transfer", amount, currency, recipient, TransactionStatus.FAILED)) + _transactions.value
        }
    }

    fun calculateQuote(fromCurrency: String, toCurrency: String, amount: Double) {
        _quote.value = repository.generateQuote(fromCurrency, toCurrency, amount)
    }
}
