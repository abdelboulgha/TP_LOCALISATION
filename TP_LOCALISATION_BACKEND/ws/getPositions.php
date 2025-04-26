<?php
include_once '../service/PositionService.php'; // adapte le chemin selon ton projet

$service = new PositionService();
$positions = $service->getAll();

$data = array();
foreach ($positions as $position) {
    $data[] = array(
        "id" => $position->getId(),
        "latitude" => $position->getLatitude(),
        "longitude" => $position->getLongitude(),
        "date" => $position->getDate(),
        "imei" => $position->getImei()
    );
}

echo json_encode($data);
?>
