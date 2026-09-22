package com.bedober.payme.payments

import android.content.Context
import android.util.Base64
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Builds the API client for Payme's secure backend. Do not replace this with
 * provider secret keys in the app; mobile-money and card secrets belong on the server.
 */
object PaymentsClient {
    fun create(context: Context, baseUrl: String): PaymentsRepository {
        val client = OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                val request: Request = chain.request().newBuilder()
                    .header("Accept", "application/json")
                    .build()
                chain.proceed(request)
            })
            .build()

        val api = Retrofit.Builder()
            .baseUrl(baseUrl.trimEnd('/') + "/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(PaymePaymentsApi::class.java)

        return PaymentsRepository(api)
    }
}
