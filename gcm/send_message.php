<?php

if (isset($_GET["regId"]) && isset($_GET["message"])) {
    $regId   = $_GET["regId"];
    $message = $_GET["message"];
    
    include_once './GCM.php';
    
    $gcm = new GCM();

    $registrationIds = [$regId];
    $message         = ["price" => $message];

    $result          = $gcm->send_notification($registrationIds, $message);

    echo $result;
}

