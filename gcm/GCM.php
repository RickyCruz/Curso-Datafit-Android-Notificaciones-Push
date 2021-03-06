<?php

class GCM
{

    function __construct()
    {
    }

    public function send_notification($registration_ids, $message)
    {
        include_once './config/config.php';

        $url = 'https://android.googleapis.com/gcm/send';

        $fields = [
            'registration_ids' => $registration_ids,
            'data'             => $message,
        ];

        $headers = [
            'Authorization: key=' . GOOGLE_API_KEY,
            'Content-Type: application/json'
        ];

        $ch = curl_init();

        // Indicar url, numero de POST vars, POST data
        curl_setopt($ch, CURLOPT_URL, $url);

        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

        // Deshabilitar temporalmente el certificado SSL 
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);

        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));

        // Ejecutar el llamado
        $result = curl_exec($ch);

        if ($result === FALSE) {
            die('Curl failed: ' . curl_error($ch));
        }

        curl_close($ch);

        echo $result;
    }

}
