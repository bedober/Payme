<?php
require __DIR__ . '/config.php';
requireAuth();
$user = getCurrentUser();
if (!$user) {
    session_destroy();
    header('Location: /web/login.php');
    exit;
}

$wallets = dbConnect()->prepare('SELECT * FROM wallets WHERE user_id = ? ORDER BY currency');
$wallets->execute([$user['id']]);
$walletRows = $wallets->fetchAll();

$total = 0.0;
foreach ($walletRows as $row) {
    $total += (float) $row['balance'];
}

$txStmt = dbConnect()->prepare('SELECT * FROM transactions WHERE user_id = ? ORDER BY created_at DESC LIMIT 10');
$txStmt->execute([$user['id']]);
$transactions = $txStmt->fetchAll();
?>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Payme Dashboard</title>
  <link rel="stylesheet" href="/web/assets/app.css" />
</head>
<body>
  <div class="app-shell">
    <aside class="sidebar">
      <div class="brand">Payme</div>
      <nav>
        <a class="active" href="/web/dashboard.php">Dashboard</a>
        <a href="/web/dashboard.php#wallets">Wallets</a>
        <a href="/web/dashboard.php#activity">Activity</a>
        <a href="/web/login.php?logout=1">Logout</a>
      </nav>
    </aside>

    <main class="main-panel">
      <header class="topbar">
        <div>
          <p class="eyebrow">Welcome back</p>
          <h1><?= htmlspecialchars($user['full_name']) ?></h1>
        </div>
        <button class="primary-btn">Add funds</button>
      </header>

      <section class="summary-grid">
        <div class="card highlight">
          <span>Total balance</span>
          <strong>$<?= number_format($total, 2) ?></strong>
        </div>
        <div class="card">
          <span>Income</span>
          <strong>$4,200.00</strong>
        </div>
        <div class="card">
          <span>Expenses</span>
          <strong>$2,930.95</strong>
        </div>
      </section>

      <section id="wallets" class="card section-block">
        <div class="section-header">
          <h2>Wallets</h2>
          <button class="secondary-btn">New wallet</button>
        </div>
        <div class="wallet-list">
          <?php foreach ($walletRows as $wallet): ?>
            <div class="wallet-item">
              <div>
                <h3><?= htmlspecialchars($wallet['currency']) ?></h3>
                <p><?= htmlspecialchars($wallet['status']) ?></p>
              </div>
              <strong><?= number_format((float) $wallet['balance'], 2) ?> <?= htmlspecialchars($wallet['currency']) ?></strong>
            </div>
          <?php endforeach; ?>
        </div>
      </section>

      <section id="activity" class="card section-block">
        <div class="section-header">
          <h2>Recent activity</h2>
        </div>
        <div class="activity-list">
          <?php foreach ($transactions as $tx): ?>
            <div class="activity-row">
              <div>
                <h4><?= htmlspecialchars($tx['type']) ?></h4>
                <p><?= htmlspecialchars($tx['counterparty'] ?: 'Internal transfer') ?></p>
              </div>
              <div class="activity-meta">
                <strong><?= htmlspecialchars($tx['currency']) ?> <?= number_format((float) $tx['amount'], 2) ?></strong>
                <span><?= htmlspecialchars($tx['status']) ?></span>
              </div>
            </div>
          <?php endforeach; ?>
        </div>
      </section>
    </main>
  </div>
</body>
</html>
