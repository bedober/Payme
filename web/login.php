<?php
require __DIR__ . '/config.php';

if (isset($_GET['logout'])) {
    session_destroy();
    header('Location: /web/login.php');
    exit;
}

if (isset($_SESSION['user_id'])) {
    header('Location: /web/dashboard.php');
    exit;
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $email = trim((string) ($_POST['email'] ?? ''));
    $password = (string) ($_POST['password'] ?? '');

    try {
        $stmt = dbConnect()->prepare('SELECT * FROM users WHERE email = ? LIMIT 1');
        $stmt->execute([strtolower($email)]);
        $user = $stmt->fetch();

        if ($user && password_verify($password, $user['password_hash'])) {
            $_SESSION['user_id'] = $user['id'];
            header('Location: /web/dashboard.php');
            exit;
        }

        $error = 'Invalid email or password.';
    } catch (Throwable $e) {
        $error = 'Unable to sign in right now.';
    }
}
?>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Payme Login</title>
  <link rel="stylesheet" href="/web/assets/app.css" />
</head>
<body class="auth-page">
  <div class="auth-card">
    <div class="auth-brand">Payme</div>
    <h1>Sign in</h1>
    <?php if (!empty($error)): ?>
      <p class="error-message"><?= htmlspecialchars($error) ?></p>
    <?php endif; ?>
    <form method="post" class="auth-form">
      <label>
        Email
        <input type="email" name="email" required />
      </label>
      <label>
        Password
        <input type="password" name="password" required />
      </label>
      <button type="submit" class="primary-btn">Login</button>
    </form>
    <p class="subtle">Demo account must exist in MySQL before login.</p>
  </div>
</body>
</html>
