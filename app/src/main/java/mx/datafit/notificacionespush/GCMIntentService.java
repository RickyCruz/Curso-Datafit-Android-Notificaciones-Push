package mx.datafit.notificacionespush;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

import mx.datafit.notificacionespush.helpers.ServerUtilities;

import static mx.datafit.notificacionespush.helpers.CommonUtilities.SENDER_ID;
import static mx.datafit.notificacionespush.helpers.CommonUtilities.displayMessage;

public class GCMIntentService extends GCMBaseIntentService {

    private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super(SENDER_ID);
    }

    /*
     * Método llamado en el dispositivo registrado
     */
    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Dispositivo Registrado: regId = " + registrationId);
        displayMessage(context, "Dispositivo habilitado para recibir notificaciones GCM");
        Log.d("Activity", MainActivity.name);
        ServerUtilities.register(context, MainActivity.name, MainActivity.email, registrationId);
    }

    /*
     * Método llamado en el dispositivo no registrado
     */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Dispositivo no registrado");
        displayMessage(context, getString(R.string.gcm_unregistered));
        ServerUtilities.unregister(context, registrationId);
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Mensaje recibido");
        String message = intent.getExtras().getString("price");

        displayMessage(context, message);
        // notificar al usuario
        generateNotification(context, message);
    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Borrar mensaje de notificación recibido");
        String message = getString(R.string.gcm_deleted, total);
        displayMessage(context, message);
        // notificar usuario
        generateNotification(context, message);
    }

    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Error recibido: " + errorId);
        displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        Log.i(TAG, "Error recibido recuperable: " + errorId);
        displayMessage(context, getString(R.string.gcm_recoverable_error,
                errorId));
        return super.onRecoverableError(context, errorId);
    }

    /*
     * Emite una notificación para informar al usuario que el servidor ha enviado un mensaje.
     */
    @SuppressWarnings("deprecation")
    private static void generateNotification(Context context, String message) {
        int icon = mx.datafit.notificacionespush.R.mipmap.ic_launcher;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);

        long[] vibrate = { 100, 100, 200, 300, 100, 200, 700 };

        String title = context.getString(R.string.app_name) + " dice: ";

        Intent notificationIntent = new Intent(context, NotificationActivity.class);

        // Establece el Intent por lo que no se pone en marcha una nueva actividad
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        //La notificación se detendrá cuando el usuario pulse en ella
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // Sonido de notificaciones por defecto
        notification.defaults |= Notification.DEFAULT_SOUND;

        // Vibración
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.vibrate = vibrate;

        // Luz LED
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        // Intenta establecer el color y el parpadeo del lED
        try {
            notification.ledARGB  = 0xff00ff00;
            notification.ledOnMS  = 300;
            notification.ledOffMS = 1000;
            notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        } catch (Exception ex) {
            // ¯\_(ツ)_/¯
        }
        //Lanzar notificación
        notificationManager.notify(0, notification);
    }
}