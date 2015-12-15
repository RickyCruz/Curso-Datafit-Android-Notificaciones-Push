package mx.datafit.notificacionespush;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static mx.datafit.notificacionespush.helpers.CommonUtilities.SENDER_ID;
import static mx.datafit.notificacionespush.helpers.CommonUtilities.SERVER_URL;

import mx.datafit.notificacionespush.helpers.ConnectionDetector;

public class RegisterActivity extends AppCompatActivity {

    private EditText txtName;
    private EditText txtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());

        if (!cd.isConnectingToInternet()) {
            showAlertDialog("Error de conexión", "Por favor conectate a Internet (Wi-Fi/3G");
            return;
        }

        txtName     = (EditText) findViewById(R.id.txtName);
        txtEmail    = (EditText) findViewById(R.id.txtEmail);
        Button btnRegister = (Button) findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(register);
    }

    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(RegisterActivity.this);
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

    private View.OnClickListener register = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            String name  = txtName.getText().toString();
            String email = txtEmail.getText().toString();

            if (name.trim().length() > 0 && email.trim().length() > 0) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("email", email);
                startActivity(intent);
                finish();
            } else {
                showAlertDialog("Error", "Ha ocurrido un error en tu registro, intenta de nuevo.");
            }
        }
    };
}

