<?php

$json = array();

/**
 * Registrar el dispositivo del usuario
 * almacenando su id de registro en la BD
 */
if (isset($_POST["name"]) && isset($_POST["email"]) && isset($_POST["regId"])) {

    $name      = $_POST["name"];
    $email     = $_POST["email"];
    $gcm_regid = $_POST["regId"]; // GCM Registration ID

    include_once './model/User.php';
    include_once './GCM.php';

    $gcm = new GCM();

    $res = User::storeUser($name, $email, $gcm_regid);

    $registration_id = [$gcm_regid];
    $message         = ["product" => "shirt"];

    $result = $gcm->send_notification($registration_id, $message);

    echo $result;
} else {
    echo "Error blah blah blah...";
}