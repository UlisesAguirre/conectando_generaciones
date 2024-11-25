package com.example.tpint_grupo2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tpint_grupo2.adapter.PerfilCompatibleAdapter;
import com.example.tpint_grupo2.conexion.DataPerfilCompatible;
import com.example.tpint_grupo2.entidades.PerfilCompatible;
import com.example.tpint_grupo2.entidades.Usuario;
import com.example.tpint_grupo2.sessions.UsuariosCompatiblesSession;
import com.example.tpint_grupo2.sessions.UsuariosSession;

import java.util.ArrayList;
import java.util.List;

public class PerfilesCompatiblesFragment extends Fragment {

    private RecyclerView recyclerViewPerfiles;
    private List<PerfilCompatible> perfilesCompatibles;
    private PerfilCompatibleAdapter perfilCompatibleAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfiles_compatibles, container, false);

        // Inicializar vistas del layout
        recyclerViewPerfiles = view.findViewById(R.id.recyclerViewPerfiles);

        // Configurar RecyclerView
        recyclerViewPerfiles.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inicializar el adaptador con una lista vacía
        perfilesCompatibles = new ArrayList<>();
        perfilCompatibleAdapter = new PerfilCompatibleAdapter(perfilesCompatibles);
        recyclerViewPerfiles.setAdapter(perfilCompatibleAdapter);

        // Cargar perfiles compatibles del usuario logueado
        cargarPerfilesCompatibles();

        // Configurar el evento para el botón "Coordinar Actividad"
        Button btnCoordinar = view.findViewById(R.id.btn_coordinar);
        btnCoordinar.setOnClickListener(v -> {
            // Obtener el perfil seleccionado del adaptador
            PerfilCompatible perfilSeleccionado = perfilCompatibleAdapter.getPerfilSeleccionado();

            if (perfilSeleccionado != null) {
                // Guardar el perfil en la sesión
                UsuariosCompatiblesSession session = new UsuariosCompatiblesSession(getContext());
                session.savePerfilCompatible(perfilSeleccionado);

                // Ir al fragmento de coordinar actividad
                CoordinarActividadFragment coordinarActividadFragment = new CoordinarActividadFragment();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_menu_principal_container, coordinarActividadFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                // Mostrar un mensaje indicando que no se ha seleccionado ningún perfil
                Toast.makeText(getContext(), "Por favor, seleccione un perfil para coordinar la actividad.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void cargarPerfilesCompatibles() {
        // Limpiar la lista de perfiles antes de cargar nuevos datos
        perfilesCompatibles.clear();
        perfilCompatibleAdapter.notifyDataSetChanged();

        // Obtener el número de documento del usuario logueado
        UsuariosSession userSession = new UsuariosSession(getContext());
        Usuario usuario = userSession.getUser();
        String nroDocumento = String.valueOf(usuario.getNro_documento());

        // Crear la instancia de la clase de conexión que maneja los perfiles
        DataPerfilCompatible dataPerfilCompatible = new DataPerfilCompatible(getContext());

        // Obtener los perfiles compatibles desde la base de datos
        dataPerfilCompatible.obtenerPerfilesCompatibles(nroDocumento, new DataPerfilCompatible.PerfilesCompatiblesCallback() {
            @Override
            public void onPerfilesCompatiblesCargados(List<PerfilCompatible> perfiles) {
                if (perfiles != null) {
                    // Actualizar la lista de perfiles y el adaptador
                    perfilesCompatibles.addAll(perfiles);
                    perfilCompatibleAdapter.setPerfiles(perfilesCompatibles);
                    perfilCompatibleAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
