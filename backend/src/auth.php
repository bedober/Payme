<?php
declare(strict_types=1);
require dirname(__DIR__).'/src/bootstrap.php';
use Firebase\JWT\JWT; use Firebase\JWT\Key;
function token(array $u): string { $n=time(); return JWT::encode(['iss'=>env('JWT_ISSUER','payme-api'),'iat'=>$n,'exp'=>$n+(int)env('JWT_TTL_SECONDS','3600'),'sub'=>$u['id'],'email'=>$u['email']],env('JWT_SECRET'),'HS256'); }
function userId(): string { $h=$_SERVER['HTTP_AUTHORIZATION']??''; if(!preg_match('/^Bearer\s+(.+)$/i',$h,$m)) fail('Authentication required',401); try { return (string)JWT::decode($m[1],new Key(env('JWT_SECRET'),'HS256'))->sub; } catch(Throwable) { fail('Invalid or expired token',401); } }
