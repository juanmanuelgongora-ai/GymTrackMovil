<?php
require_once 'db_connect.php';

// Permitir peticiones GET
if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    http_response_code(405);
    echo json_encode(["error" => "Método no permitido. Use GET."]);
    exit();
}

try {
    // Consultar las rutinas activas en la base de datos
    $stmt = $pdo->query("SELECT id, plan_semanal, activa, created_at FROM rutinas WHERE activa = 1 LIMIT 20");
    $dbRoutines = $stmt->fetchAll();

    $routines = [];
    $counter = 1;

    foreach ($dbRoutines as $row) {
        $name = "Rutina Especializada #" . $counter;
        $description = "Plan semanal de ejercicios personalizados y acondicionamiento.";

        // Intentar parsear el JSON de 'plan_semanal' para extraer nombres descriptivos si existen
        if (!empty($row['plan_semanal'])) {
            $decoded = json_decode($row['plan_semanal'], true);
            
            // Si el JSON contiene información estructurada (por ejemplo, título u objetivo)
            if ($decoded && is_array($decoded)) {
                if (isset($decoded['nombre'])) {
                    $name = $decoded['nombre'];
                } elseif (isset($decoded['title'])) {
                    $name = $decoded['title'];
                }
                
                if (isset($decoded['descripcion'])) {
                    $description = $decoded['descripcion'];
                } elseif (isset($decoded['description'])) {
                    $description = $decoded['description'];
                } elseif (isset($decoded['dias']) && is_array($decoded['dias'])) {
                    // Si contiene una lista de días, creamos una descripción resumida
                    $description = "Entrenamiento para: " . implode(", ", array_keys($decoded['dias']));
                }
            }
        }

        // Puesto que la app móvil espera un 'id' numérico (int), generamos un índice incremental
        // y nos aseguramos de sanitizar textos.
        $routines[] = [
            "id" => $counter,
            "name" => htmlspecialchars($name),
            "description" => htmlspecialchars($description)
        ];
        
        $counter++;
    }

    // Si la base de datos está vacía, proveer rutinas por defecto para que la app nunca se vea vacía
    if (empty($routines)) {
        $routines[] = [
            "id" => 1,
            "name" => "Rutina de Acondicionamiento General",
            "description" => "Lunes: Pecho y Tríceps. Miércoles: Espalda y Bíceps. Viernes: Pierna Completa."
        ];
        $routines[] = [
            "id" => 2,
            "name" => "Rutina Full Body (Cuerpo Completo)",
            "description" => "Tres días a la semana de entrenamiento dinámico multiarticular para hipertrofia y fuerza."
        ];
    }

    http_response_code(200);
    echo json_encode($routines);

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        "error" => "Error al obtener las rutinas de ejercicio.",
        "details" => $e->getMessage()
    ]);
}
?>
