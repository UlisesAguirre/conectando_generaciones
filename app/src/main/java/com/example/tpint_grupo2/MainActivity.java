package com.example.tpint_grupo2;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tpint_grupo2.conexion.DataUsuarios;
import com.example.tpint_grupo2.helpers.SQLite_OpenHelper;

public class MainActivity extends AppCompatActivity {

    private Button btnIniciarSesion;
    private Button btnRegistrarse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Seteo de mensajes
        TextView txt_msj_ppal_p1 = findViewById(R.id.txt_msj_ppal_p1);
        String texto1_1 = "Un <font color=#00b300> pequeño gesto </font> puede";
        txt_msj_ppal_p1.setText(Html.fromHtml(texto1_1));

        TextView txt_msj_ppal_p2 = findViewById(R.id.txt_msj_ppal_p2);
        String texto1_2 = "cambiar un <font color=#00b300> día </font>";
        txt_msj_ppal_p2.setText(Html.fromHtml(texto1_2));

        TextView txt_msj2_ppal_p1 = findViewById(R.id.txt_msj2_ppal_p1);
        String texto2_1 = "<font color=#2196F3> Una conexión </font>";
        txt_msj2_ppal_p1.setText(Html.fromHtml(texto2_1));

        TextView txt_msj2_ppal_p2 = findViewById(R.id.txt_msj2_ppal_p2);
        String texto2_2 = "puede cambiar <b><font color=#00b300> una vida </font></b>";
        txt_msj2_ppal_p2.setText(Html.fromHtml(texto2_2));

        btnIniciarSesion = findViewById(R.id.btn_iniciarSesion);
        btnRegistrarse = findViewById(R.id.btn_registrarse);
        EditText editTextNroDocumento = findViewById(R.id.et_dni);
        EditText editTextContrasena = findViewById(R.id.et_password);

        // Verificar si existe un usuario activo en SQLite
        SQLite_OpenHelper dbHelper = new SQLite_OpenHelper(this);
        dbHelper.abrir();

        // Obtener datos del usuario con estado activo
        String[] datosUsuario = dbHelper.obtenerUsuarioActivo();
        dbHelper.cerrar();

        if (datosUsuario != null) {
            // Rellenar los EditText automáticamente
            editTextNroDocumento.setText(datosUsuario[0]); // Número de documento
            editTextContrasena.setText(datosUsuario[1]);  // Contraseña

            btnIniciarSesion.post(() -> btnIniciarSesion.performClick());
        }

        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nroDocumento = editTextNroDocumento.getText().toString();
                String contrasena = editTextContrasena.getText().toString();

                DataUsuarios dataUsuarios = new DataUsuarios(MainActivity.this);
                dataUsuarios.checkUserExists(nroDocumento, contrasena);
            }
        });

        btnRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SeleccionarPerfilActivity.class);
                startActivity(intent);
            }
        });
    }
}