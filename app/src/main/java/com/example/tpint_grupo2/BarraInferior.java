package com.example.tpint_grupo2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class BarraInferior extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_barra_inferior, container, false);

        // Configurar los listeners para los botones
        view.findViewById(R.id.btnBuscarPerfiles).setOnClickListener(v -> navigateToSection("BuscarPerfiles"));
        view.findViewById(R.id.btnMiPerfil).setOnClickListener(v -> navigateToSection("MiPerfil"));
        view.findViewById(R.id.btnNotificaciones).setOnClickListener(v -> navigateToSection("Notificaciones"));
        view.findViewById(R.id.btnMiAgenda).setOnClickListener(v -> navigateToSection("MiAgenda"));
        view.findViewById(R.id.btnInformes).setOnClickListener(v -> navigateToSection("Informes"));

        return view;
    }

    private void navigateToSection(String section) {

        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;

        switch (section) {
            case "BuscarPerfiles":
                fragmentManager = getParentFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_menu_principal_container, new PerfilesCompatiblesFragment());
                fragmentTransaction.addToBackStack(null); // Agrega a la pila de retroceso para que el usuario pueda volver
                fragmentTransaction.commit();
                break;
            case "MiPerfil":
                fragmentManager = getParentFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_menu_principal_container, new MiPerfilFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case "Notificaciones":
                fragmentManager = getParentFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_menu_principal_container, new NotificacionesFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case "MiAgenda":
                fragmentManager = getParentFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_menu_principal_container, new AgendaFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case "Informes":
                fragmentManager = getParentFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_menu_principal_container, new FragmentMenuInformes());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            default:
                break;
        }
    }

}
