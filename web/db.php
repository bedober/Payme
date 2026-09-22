<?php
require __DIR__ . '/config.php';

function getCurrentUser(): ?array {
    if (!isset($_SESSION['user_id'])) {
        return null;
    }

    try {
        $stmt = dbConnect()->prepare('SELECT * FROM users WHERE id = ? LIMIT 1');
        $stmt->execute([$_SESSION['user_id']]);
        return $stmt->fetch() ?: null;
    } catch (Throwable $e) {
        return null;
    }
}
