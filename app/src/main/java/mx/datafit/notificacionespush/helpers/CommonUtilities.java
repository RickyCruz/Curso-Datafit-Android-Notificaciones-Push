package mx.datafit.notificacionespush.helpers;

import android.content.Context;
import android.content.Intent;

public class CommonUtilities {

    public static final String SERVER_URL = "http://PON_TU_DIRECCIÓN_IP_AQUI/gcm/register.php";
    public static final String SENDER_ID = "TU_ID_DE_GOOGLE";

    public static final String TAG = "Datafit Notificaciones Push";

    public static final String DISPLAY_MESSAGE_ACTION =
            "mx.datafit.notificacionespush.DISPLAY_MESSAGE";

    public static final String EXTRA_MESSAGE = "message";

    /*
     * Notifica a la interfaz de usuario para mostrar un mensaje.
     * Este método se define como un ayudante común por que es usada tanto por
     * la interfaz de usuario como por el servicio en segundo plano.
     */
    public static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}