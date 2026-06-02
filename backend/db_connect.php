<?php
// Habilitar CORS para permitir peticiones desde la app móvil
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET, POST, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With");
header("Content-Type: application/json; charset=UTF-8");

// Si es una petición preflight de CORS (OPTIONS), responder de inmediato con 200 OK
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Configuración de la base de datos de Hostinger
// IMPORTANTE: Reemplaza estos valores con tus datos reales de Hostinger
define('DB_HOST', 'localhost'); // Por lo general es localhost en Hostinger, o la IP del servidor de BD
define('DB_USER', 'TU_USUARIO_DE_BASE_DE_DATOS'); // Ejemplo: u998584750_gymtrack_user
define('DB_PASS', 'TU_CONTRASEÑA_DE_BASE_DE_DATOS');
define('DB_NAME', 'u998584750_gymtrack_db'); // Veo en tu captura que se llama u998584750_gymtrack_db

try {
    $dsn = "mysql:host=" . DB_HOST . ";dbname=" . DB_NAME . ";charset=utf8mb4";
    $options = [
        PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
        PDO::ATTR_EMULATE_PREPARES   => false,
    ];
    $pdo = new PDO($dsn, DB_USER, DB_PASS, $options);
} catch (PDOException $e) {
    // Si la conexión falla, devolver un error 500 con formato JSON
    http_response_code(500);
    echo json_encode([
        "error" => "Error de conexión con la base de datos.",
        "details" => $e->getMessage()
    ]);
    exit();
}

// Función auxiliar global para generar UUIDs v4 de manera compatible en PHP nativo
function generate_uuid() {
    return sprintf('%04x%04x-%04x-%04x-%04x-%04x%04x%04x',
        mt_rand(0, 0xffff), mt_rand(0, 0xffff),
        mt_rand(0, 0xffff),
        mt_rand(0, 0x0fff) | 0x4000,
        mt_rand(0, 0x3fff) | 0x8000,
        mt_rand(0, 0xffff), mt_rand(0, 0xffff), mt_rand(0, 0xffff)
    );
}
?>
