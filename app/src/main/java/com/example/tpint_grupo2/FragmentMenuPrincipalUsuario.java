package com.example.tpint_grupo2;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentMenuPrincipalUsuario extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_principal_usuario, container, false);

        view.findViewById(R.id.btn_buscar_perfiles).setOnClickListener(v -> navigateToBuscarPerfiles());
        view.findViewById(R.id.btn_mi_perfil).setOnClickListener(v -> navigateToMiPerfil());
        view.findViewById(R.id.btn_notificaciones).setOnClickListener(v -> navigateToNotificaciones());
        view.findViewById(R.id.btn_mi_agenda).setOnClickListener(v -> navigateToMiAgenda());
        view.findViewById(R.id.btn_informes).setOnClickListener(v -> navigateToInformes());

        return view;
    }

    private void navigateToBuscarPerfiles() {
        // Reemplaza el contenedor actual con PerfilesCompatiblesFragment
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_menu_principal_container, new PerfilesCompatiblesFragment());
        fragmentTransaction.addToBackStack(null); // Agrega a la pila de retroceso para que el usuario pueda volver
        fragmentTransaction.commit();
    }

    private void navigateToMiPerfil() {
        // Reemplaza el contenedor actual con MiPerfilFragment
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_menu_principal_container, new MiPerfilFragment());
        fragmentTransaction.addToBackStack(null); // Agrega a la pila de retroceso para que el usuario pueda volver
        fragmentTransaction.commit();
    }

    private void navigateToNotificaciones() {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_menu_principal_container, new NotificacionesFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void navigateToMiAgenda() {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_menu_principal_container, new AgendaFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void navigateToInformes() {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_menu_principal_container, new FragmentMenuInformes());
        transaction.addToBackStack(null);
        transaction.commit();

    }
}
