package com.bedober.payme.payments

import java.util.UUID

class PaymentsRepository(private val api: PaymePaymentsApi) {
    suspend fun collectWithMtnMomo(
        amount: String,
        currency: String,
        phoneNumber: String,
        country: String
    ): Result<PaymentResponse> = runCatching {
        api.requestMtnCollection(
            MobileMoneyRequest(amount, currency, phoneNumber, UUID.randomUUID().toString(), country)
        )
    }

    suspend fun collectWithAirtelMoney(
        amount: String,
        currency: String,
        phoneNumber: String,
        country: String
    ): Result<PaymentResponse> = runCatching {
        api.requestAirtelCollection(
            MobileMoneyRequest(amount, currency, phoneNumber, UUID.randomUUID().toString(), country)
        )
    }

    suspend fun createCardPayment(
        amountMinor: Long,
        currency: String,
        customerReference: String? = null
    ): Result<CardPaymentIntentResponse> = runCatching {
        api.createCardPaymentIntent(
            CardPaymentIntentRequest(
                amountMinor = amountMinor,
                currency = currency,
                idempotencyKey = UUID.randomUUID().toString(),
                customerReference = customerReference
            )
        )
    }
}
