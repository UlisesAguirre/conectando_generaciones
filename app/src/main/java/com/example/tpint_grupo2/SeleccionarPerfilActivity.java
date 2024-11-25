package com.example.tpint_grupo2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SeleccionarPerfilActivity extends AppCompatActivity {

    private ImageButton imageButton1, imageButton2;
    private ImageButton selectedButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seleccionarperfil);

        imageButton1 = findViewById(R.id.imageButton);
        imageButton2 = findViewById(R.id.imageButton2);

        imageButton1.setOnClickListener(v -> selectImageButton(imageButton1));
        imageButton2.setOnClickListener(v -> selectImageButton(imageButton2));
        Button btnContinuar = findViewById(R.id.btn_iniciarSesion);

        btnContinuar.setOnClickListener(v -> {
            Intent intent;
            if (selectedButton == imageButton1) {
                intent = new Intent(SeleccionarPerfilActivity.this, RegistrarseVoluntarioActivity.class);
            } else if (selectedButton == imageButton2) {
                intent = new Intent(SeleccionarPerfilActivity.this, RegistrarseAdultoActivity.class);
            } else {
                return;
            }
            startActivity(intent);
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void selectImageButton(ImageButton button) {
        if (button == selectedButton) {
            return;
        }

        if (selectedButton != null) {
            selectedButton.setBackgroundResource(android.R.drawable.btn_default);
            selectedButton.setScaleX(1f);
            selectedButton.setScaleY(1f);
        }

        selectedButton = button;

        selectedButton.setScaleX(1.1f);
        selectedButton.setScaleY(1.1f);
    }
}
