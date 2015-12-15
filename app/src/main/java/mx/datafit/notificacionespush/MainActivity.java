package mx.datafit.notificacionespush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

import mx.datafit.notificacionespush.helpers.ConnectionDetector;
import mx.datafit.notificacionespush.helpers.ServerUtilities;
import mx.datafit.notificacionespush.helpers.WakeLocker;

import static mx.datafit.notificacionespush.helpers.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static mx.datafit.notificacionespush.helpers.CommonUtilities.EXTRA_MESSAGE;
import static mx.datafit.notificacionespush.helpers.CommonUtilities.SENDER_ID;

public class MainActivity extends AppCompatActivity {

    private AsyncTask<Void, Void, Void> mRegisterTask;

    public static String name;
    public static String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());

        if (!cd.isConnectingToInternet()) {
            showAlertDialog("Error de conexión", "Por favor conectate a Internet (Wi-Fi/3G");
            return;
        }

        Intent i = getIntent();

        name = i.getStringExtra("name");
        email = i.getStringExtra("email");

        // Debemos asegurarnos de que el dispositivo cuenta con las dependencias correspondientes.
        GCMRegistrar.checkDevice(this);
        // Asegúrarse de que el manifiesto ha sido configurado correctamente
        // Mientras aplicación este en desarrollo, al subir a la PlayStore eliminarlo
        GCMRegistrar.checkManifest(this);
        registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));
        // Obtener id Registro GCM
        final String regId = GCMRegistrar.getRegistrationId(this);
        android.util.Log.i("REG ID GCM", regId);
        // Checar si regId ya esta presente
        if (regId.equals("")) { // Registro no está presente, registrar ahora con GCM
            GCMRegistrar.register(this, SENDER_ID);
        } else { // Dispositivo ya esta registrado en GCM
            if (GCMRegistrar.isRegisteredOnServer(this)) { // Saltar registro
                Toast.makeText(MainActivity.this, "¡Bienvenido!", Toast.LENGTH_SHORT).show();
            } else {
                // Intenta registrar de nuevo, pero no en el hilo de interfaz de usuario.
                // También es necesario cancelar la OnDestroy hilo (),
                // por lo tanto el uso de AsyncTask en lugar de un hilo en bruto.
                final Context context = this;
                mRegisterTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        // Registrar en nuestro servidor
                        // El servidor crea/actualiza un nuevo usuario
                        ServerUtilities.register(context, name, email, regId);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                    }
                };
                mRegisterTask.execute(null, null, null);
            }
        }

    }

    /*
     * Recibiendo mensaje Push
     */
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
            // Despertar móvil si está durmiendo
            WakeLocker.acquire(getApplicationContext());
            Intent i = new Intent(MainActivity.this, NotificationActivity.class);
            i.putExtra("msg", newMessage + "\n\n");
            startActivity(i);
            //"Haz recibido un mensaje", Toast.LENGTH_LONG).show();
            // Liberar bloqueo wake lock
            WakeLocker.release();
        }
    };

    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setIcon(R.mipmap.ic_launcher);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.create();
        alert.show();
    }
}
