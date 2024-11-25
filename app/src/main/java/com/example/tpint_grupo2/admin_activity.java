package com.example.tpint_grupo2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class admin_activity extends AppCompatActivity {

    private ImageButton btnInicio,btnPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnInicio=findViewById(R.id.btnInicio);
        btnPerfil=findViewById(R.id.btnMiPerfil);

       btnInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentMenuAdmin objInicioAdmin=new FragmentMenuAdmin();
                FragmentTransaction fragmentTransaction= getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainerView2,objInicioAdmin);
                fragmentTransaction.addToBackStack(null); //PERMITE VOLVER ATRÁS AL OTRO FRAGMENTO
                fragmentTransaction.commit();
            }
        });
        btnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentPerfilAdministrador objMiPerfil=new FragmentPerfilAdministrador();
                FragmentTransaction fragmentTransaction= getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainerView2,objMiPerfil);
                fragmentTransaction.addToBackStack(null); //PERMITE VOLVER ATRÁS AL OTRO FRAGMENTO
                fragmentTransaction.commit();
            }
        });
    }
}