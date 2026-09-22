<?php
declare(strict_types=1);
require dirname(__DIR__).'/vendor/autoload.php';
Dotenv\Dotenv::createImmutable(dirname(__DIR__))->safeLoad();
function env(string $k, ?string $d=null): string { return $_ENV[$k] ?? $d ?? ''; }
function db(): PDO { static $p; if (!$p) { $p=new PDO('mysql:host='.env('DB_HOST','127.0.0.1').';port='.env('DB_PORT','3306').';dbname='.env('DB_NAME','payme').';charset=utf8mb4',env('DB_USER'),env('DB_PASSWORD'),[PDO::ATTR_ERRMODE=>PDO::ERRMODE_EXCEPTION,PDO::ATTR_DEFAULT_FETCH_MODE=>PDO::FETCH_ASSOC,PDO::ATTR_EMULATE_PREPARES=>false]); } return $p; }
function input(): array { $v=json_decode(file_get_contents('php://input'),true); return is_array($v)?$v:[]; }
function respond(mixed $v,int $s=200): never { http_response_code($s); header('Content-Type: application/json'); echo json_encode($v,JSON_UNESCAPED_SLASHES); exit; }
function fail(string $m,int $s=400): never { respond(['error'=>$m],$s); }
function uuid(): string { $d=random_bytes(16); $d[6]=chr((ord($d[6])&15)|64); $d[8]=chr((ord($d[8])&63)|128); return vsprintf('%s%s-%s-%s-%s-%s%s%s',str_split(bin2hex($d),4)); }
