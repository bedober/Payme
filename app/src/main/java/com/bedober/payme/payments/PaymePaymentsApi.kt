package com.bedober.payme.payments

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Provider contracts are intentionally called through Payme's backend proxy.
 * MTN and Airtel credentials must never be embedded in the Android APK.
 */
interface PaymePaymentsApi {
    @POST("v1/payments/mtn-momo/collections")
    suspend fun requestMtnCollection(@Body request: MobileMoneyRequest): PaymentResponse

    @POST("v1/payments/airtel-money/collections")
    suspend fun requestAirtelCollection(@Body request: MobileMoneyRequest): PaymentResponse

    @POST("v1/payments/card/payment-intents")
    suspend fun createCardPaymentIntent(@Body request: CardPaymentIntentRequest): CardPaymentIntentResponse

    @POST("v1/payments/{provider}/payouts")
    suspend fun requestPayout(@Path("provider") provider: String, @Body request: PayoutRequest): PaymentResponse

    @POST("v1/payments/{provider}/payments/{paymentId}/cancel")
    suspend fun cancel(@Path("provider") provider: String, @Path("paymentId") paymentId: String): PaymentResponse

    @retrofit2.http.GET("v1/payments/{provider}/payments/{paymentId}")
    suspend fun status(@Path("provider") provider: String, @Path("paymentId") paymentId: String): PaymentStatusResponse
}

data class MobileMoneyRequest(
    val amount: String,
    val currency: String,
    val phoneNumber: String,
    val externalReference: String,
    val country: String
)

data class PayoutRequest(
    val amount: String,
    val currency: String,
    val phoneNumber: String,
    val externalReference: String,
    val country: String
)

data class CardPaymentIntentRequest(
    val amountMinor: Long,
    val currency: String,
    val idempotencyKey: String,
    val customerReference: String? = null
)

data class PaymentResponse(
    val paymentId: String,
    val status: String,
    val message: String? = null
)

data class PaymentStatusResponse(
    val paymentId: String,
    val status: String,
    val failureReason: String? = null
)

data class CardPaymentIntentResponse(
    val paymentIntentClientSecret: String,
    val paymentId: String
)
