# Payme

Kotlin + Jetpack Compose mobile wallet starter for multi-currency balances, P2P transfers, and crypto exchange quotes.

## Payment integrations added

The Android client now includes a secure backend-proxy contract for:

- **MTN MoMo** collections and payouts
- **Airtel Money** collections and payouts
- **Credit/debit card payments** through a PaymentIntent-style flow (compatible with providers such as Stripe)
- Payment status and cancellation endpoints
- Idempotency references for payment requests

## Security architecture

Provider credentials, webhook verification, card secret keys, and mobile-money API secrets must remain on the Payme backend. The Android app calls only the Payme API and receives short-lived payment results/client secrets. Never put MTN/Airtel credentials, Stripe secret keys, or raw card data in the APK.

Before enabling live money movement, configure:

1. MTN MoMo developer credentials and the country-specific environment/base URL.
2. Airtel Money developer credentials and country-specific product configuration.
3. A PCI-compliant card processor and server-side PaymentIntent creation.
4. Webhook signature verification and idempotent transaction handling.
5. KYC/AML, limits, fraud controls, reconciliation, and licensing for each operating country.
6. `PaymentsClient.create(context, "https://api.your-domain.example")` with your HTTPS backend URL.

Provider APIs vary by country and product, so the backend must normalize provider-specific authentication, request formats, and asynchronous status callbacks.

## Run

Open the repository in Android Studio, sync Gradle, select the `app` configuration, and run on a device or emulator.
