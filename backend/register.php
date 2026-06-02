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
if (
    empty($input['name']) || empty($input['email']) || empty($input['password']) ||
    empty($input['age']) || empty($input['sex']) || empty($input['goal']) || 
    empty($input['address'])
) {
    http_response_code(400);
    echo json_encode(["error" => "Faltan datos obligatorios para el registro."]);
    exit();
}

// Extraer variables
$name = trim($input['name']);
$email = trim($input['email']);
$password = trim($input['password']);
$age = intval($input['age']);
$sex = trim($input['sex']);
$eps = isset($input['eps']) ? trim($input['eps']) : '';
$goal = trim($input['goal']);
$address = trim($input['address']);
$phone = isset($input['phone']) ? trim($input['phone']) : '';
$familyPhone = isset($input['familyPhone']) ? trim($input['familyPhone']) : '';

try {
    // 1. Verificar si el correo ya existe
    $stmtCheck = $pdo->prepare("SELECT id FROM users WHERE email = ?");
    $stmtCheck->execute([$email]);
    if ($stmtCheck->fetch()) {
        http_response_code(400);
        echo json_encode(["error" => "El correo electrónico ya está registrado."]);
        exit();
    }

    // 2. Iniciar Transacción para asegurar consistencia
    $pdo->beginTransaction();

    // Generar UUIDs
    $userId = generate_uuid();
    $clientId = generate_uuid();

    // Encriptar la contraseña de forma segura con BCRYPT
    $hashedPassword = password_hash($password, PASSWORD_BCRYPT);

    // Calcular fecha de nacimiento aproximada según la edad
    $currentYear = intval(date('Y'));
    $birthYear = $currentYear - $age;
    $birthDate = "$birthYear-01-01"; // Fecha por defecto para evitar nulo si es requerido

    $now = date('Y-m-d H:i:s');

    // 3. Insertar en la tabla 'users'
    $sqlUser = "INSERT INTO users (id, name, email, password, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";
    $stmtUser = $pdo->prepare($sqlUser);
    $stmtUser->execute([$userId, $name, $email, $hashedPassword, $now, $now]);

    // 4. Insertar en la tabla 'clientes'
    $sqlClient = "INSERT INTO clientes (
        id, user_id, gimnasio_id, ubicacion, fecha_nacimiento, genero, 
        peso_kg, altura_cm, imc, nivel_actividad, objetivo_principal, condicion_medica, activo, created_at
    ) VALUES (?, ?, NULL, ?, ?, ?, NULL, NULL, NULL, 'Medio', ?, NULL, 1, ?)";
    
    $stmtClient = $pdo->prepare($sqlClient);
    $stmtClient->execute([$clientId, $userId, $address, $birthDate, $sex, $goal, $now]);

    // Confirmar cambios
    $pdo->commit();

    http_response_code(201);
    echo json_encode(["message" => "Usuario registrado con éxito."]);

} catch (Exception $e) {
    // Si algo falla, revertir los cambios
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }
    http_response_code(500);
    echo json_encode([
        "error" => "Error al registrar el usuario.",
        "details" => $e->getMessage()
    ]);
}
?>
