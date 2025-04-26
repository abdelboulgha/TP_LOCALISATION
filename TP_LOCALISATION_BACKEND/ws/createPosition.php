<?php

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    include_once '../service/PositionService.php';
    create();
}
function create()
{
    header('Content-Type: application/json'); // ✅ informe qu'on envoie du JSON

    $latitude = $_POST['latitude'];
    $longitude = $_POST['longitude'];
    $date = $_POST['date'];
    $imei = $_POST['imei'];

    if (!$latitude || !$longitude || !$date || !$imei) {
        http_response_code(400);
        echo json_encode(['error' => 'Champs manquants']);
        return;
    }

    include_once '../service/PositionService.php';
    $ss = new PositionService();
    $created = $ss->create(new Position(1, $latitude, $longitude, $date, $imei));

    if ($created) {
        echo json_encode(['message' => 'Position enregistrée avec succès']);
    } else {
        http_response_code(500);
        echo json_encode(['error' => 'Erreur lors de l\'enregistrement']);
    }
}
