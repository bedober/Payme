<?php
session_start();

const DB_HOST = '127.0.0.1';
const DB_NAME = 'payme';
const DB_USER = 'payme';
const DB_PASSWORD = 'change-me';

$baseUrl = '/';

function dbConnect(): PDO {
    static $pdo = null;
    if ($pdo === null) {
        $pdo = new PDO(
            'mysql:host=' . DB_HOST . ';dbname=' . DB_NAME . ';charset=utf8mb4',
            DB_USER,
            DB_PASSWORD,
            [
                PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
                PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
                PDO::ATTR_EMULATE_PREPARES => false,
            ]
        );
    }
    return $pdo;
}

function requireAuth(): void {
    if (!isset($_SESSION['user_id'])) {
        header('Location: /web/login.php');
        exit;
    }
}
