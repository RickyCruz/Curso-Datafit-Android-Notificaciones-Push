package mx.datafit.notificacionespush.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import mx.datafit.notificacionespush.R;

import static mx.datafit.notificacionespush.helpers.CommonUtilities.SERVER_URL;
import static mx.datafit.notificacionespush.helpers.CommonUtilities.TAG;
import static mx.datafit.notificacionespush.helpers.CommonUtilities.displayMessage;

public class ServerUtilities {

    private static final int MAX_ATTEMPTS = 3;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();

    /*
     * Registrar la cuenta del dispositivo en el servidor.
     */
    @SuppressLint("LongLogTag")
    public static void register(final Context context, String name, String email, final String regId) {
        Log.i(TAG, "registering device (regId = " + regId + ")");
        String serverUrl = SERVER_URL;
        Map<String, String> params = new HashMap<>();
        params.put("regId", regId);
        params.put("name", name);
        params.put("email", email);

        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        // Una vez GCM devuelve un ID de registro, es necesario registrarse en nuestro servidor
        // El servidor puede estar caído, en ese caso se volverá a intentar un par de veces.
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            Log.d(TAG, "Intento #" + i + " de registro");
            try {
                displayMessage(context, context.getString(R.string.server_registering, i, MAX_ATTEMPTS));
                post(serverUrl, params);
                GCMRegistrar.setRegisteredOnServer(context, true);
                String message = context.getString(R.string.server_registered);
                CommonUtilities.displayMessage(context, message);
                return;
            } catch (IOException e) {
                // Aquí estamos simplificando y volviendo a intentar la
                // conexión en cualquier error que suceda,
                // lo ideal sería intentar sólo en errores irrecuperables
                // (Como el código de error HTTP 503).
                Log.e(TAG, "Fallo el registro en el intento " + i + ":" + e);
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Log.d(TAG, "Mandando a dormir " + backoff + " ms antes de reintentar");
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    // Activity finaliza antes de completar la accion - exit.
                    Log.d(TAG, "Thread interrumpido: abortar intentos restantes!");
                    Thread.currentThread().interrupt();
                    return;
                }
                backoff *= 2;
            }
        }
        String message = context.getString(R.string.server_register_error, MAX_ATTEMPTS);
        CommonUtilities.displayMessage(context, message);
    }

    /*
     * Anular registro de la cuenta/dispositivo de nuestro servidor
     */
    @SuppressLint("LongLogTag")
    public static void unregister(final Context context, final String regId) {
        Log.i(TAG, "Quitar registro de dispositivo (regId = " + regId + ")");
        String serverUrl = SERVER_URL + "/unregister.php";
        Map<String, String> params = new HashMap<>();
        params.put("regId", regId);
        try {
            post(serverUrl, params);
            GCMRegistrar.setRegisteredOnServer(context, false);
            String message = context.getString(R.string.server_unregistered);
            CommonUtilities.displayMessage(context, message);
        } catch (IOException e) {
            // En este punto, el dispositivo no está registrado desde GCM, pero todavía
            // esta registrado en el servidor.
            // Se podría tratar de anular el registro de nuevo, pero no es necesario:
            // Si el servidor intenta enviar un mensaje al dispositivo, se llamará
            // a "NotRegistered" mensaje de error y se debe anular el registro del dispositivo.
            String message = context.getString(R.string.server_unregister_error,
                    e.getMessage());
            CommonUtilities.displayMessage(context, message);
        }
    }

    /*
     * Emitir una solicitud POST al servidor.
     */
    @SuppressLint("LongLogTag")
    private static void post(String endpoint, Map<String, String> params) throws IOException {

        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("URL Inválida: " + endpoint);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        // Construir el cuerpo POST utilizando los parámetros
        while (iterator.hasNext()) {
            Map.Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString();
        Log.v(TAG, "POST '" + body + "' a " + url);
        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        try {
            Log.e("URL", "> " + url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            // enviar la solicitud
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            // manejar la respuesta
            int status = conn.getResponseCode();
            if (status != 200) {
                throw new IOException("Post falló con código de error " + status);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}