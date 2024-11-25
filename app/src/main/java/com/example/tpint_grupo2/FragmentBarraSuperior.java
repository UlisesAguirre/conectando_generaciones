package com.example.tpint_grupo2;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tpint_grupo2.entidades.Usuario;
import com.example.tpint_grupo2.sessions.UsuariosSession;
import com.google.android.material.appbar.MaterialToolbar;

public class FragmentBarraSuperior extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflamos el layout del fragmento
        View rootView = inflater.inflate(R.layout.fragment_barra_superior, container, false);

        ImageView iconoMenu = rootView.findViewById(R.id.icono_menu);

        // Verificamos si el contexto no es nulo antes de acceder a la sesión
        if (getContext() != null) {
            // Accedemos a la sesión del usuario
            UsuariosSession userSession = new UsuariosSession(getContext());
            Usuario usuario = userSession.getUser();

            // Verificamos si la actividad actual no es ActivityMain
            if (getActivity() instanceof MainActivity) {
                // Si estamos en ActivityMain, no hacemos nada cuando el ícono es tocado
                iconoMenu.setOnClickListener(view -> {
                    // Opcional: Mostrar un mensaje indicando que no es necesario hacer nada en esta actividad
                });
            } else {
                // Si el usuario está logueado (usuario != null)
                if (usuario != null) {
                    // Configuramos el listener para el clic en el ícono del menú
                    iconoMenu.setOnClickListener(view -> {
                        // Redirigir al nuevo fragmento
                        replaceFragment(new FragmentMenuPrincipalUsuario()); // Cambia "FragmentMenuPrincipalUsuario" por el fragmento que deseas mostrar
                    });
                } else {
                    // Si no hay usuario logueado, podemos mostrar un mensaje o no hacer nada
                    iconoMenu.setOnClickListener(view -> {
                        // Opcional: Mostrar un mensaje indicando que el usuario debe iniciar sesión
                        // Ejemplo: Toast.makeText(getContext(), "Por favor, inicie sesión", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        } else {
            // Si el contexto es nulo, no hacemos nada o puedes mostrar un mensaje de error si lo prefieres
            // Ejemplo: Toast.makeText(getActivity(), "Error al obtener el contexto", Toast.LENGTH_SHORT).show();
        }

        return rootView;
    }

    // Método para reemplazar el fragmento actual por otro
    private void replaceFragment(Fragment fragment) {
        // Usamos el FragmentTransaction para reemplazar el fragmento
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_menu_principal_container, fragment) // Usa el ID de tu contenedor de fragmentos
                .addToBackStack(null)  // Para que puedas volver atrás
                .commit();
    }
}
