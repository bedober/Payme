# Payme PHP/MySQL backend

## Requirements
- PHP 8.2+
- MySQL 8+
- Composer

## Setup
```bash
cd backend
composer install
cp .env.example .env
mysql -u root -p < database/schema.sql
php -S 127.0.0.1:8080 -t public
```

The Android app must call this backend. Provider secrets never belong in the APK.

Before production: configure provider adapters, signed webhooks, KYC/AML, PCI-compliant card tokenization, rate limits, monitoring, backups, reconciliation, and licensing.
