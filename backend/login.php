<?php
require_once 'db_connect.php';

// Solo permitir peticiones POST
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(["error" => "Método no permitido. Use POST."]);
    exit();
}

// Obtener datos del cuerpo de la petición (JSON)
$inputJSON = file_get_contents('php://input');
$input = json_decode($inputJSON, true);

// Validar campos requeridos
if (empty($input['email']) || empty($input['password'])) {
    http_response_code(400);
    echo json_encode(["error" => "El correo electrónico y la contraseña son requeridos."]);
    exit();
}

$email = trim($input['email']);
$password = trim($input['password']);

try {
    // Buscar el usuario por email
    $stmt = $pdo->prepare("SELECT id, name, email, password FROM users WHERE email = ?");
    $stmt->execute([$email]);
    $user = $stmt->fetch();

    // Validar contraseña
    if ($user && password_verify($password, $user['password'])) {
        // Generar un token aleatorio simple (o JWT si es necesario, pero un hash aleatorio es perfecto)
        $token = bin2hex(random_bytes(32));

        // Estructura de respuesta que espera la app Android
        $response = [
            "token" => $token,
            "user" => [
                "name" => $user['name'],
                "email" => $user['email']
            ]
        ];

        http_response_code(200);
        echo json_encode($response);
    } else {
        http_response_code(401);
        echo json_encode(["error" => "Credenciales inválidas. Verifique correo o contraseña."]);
    }

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        "error" => "Error interno en el servidor de inicio de sesión.",
        "details" => $e->getMessage()
    ]);
}
?>
