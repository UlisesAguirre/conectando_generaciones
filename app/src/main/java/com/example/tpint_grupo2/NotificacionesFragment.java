package com.example.tpint_grupo2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tpint_grupo2.R;
import com.example.tpint_grupo2.conexion.DataNotificaciones;
import com.example.tpint_grupo2.entidades.Notificacion;
import com.example.tpint_grupo2.adapter.NotificacionesAdapter;
import com.example.tpint_grupo2.entidades.Usuario;
import com.example.tpint_grupo2.sessions.UsuariosSession;

import java.util.List;

public class NotificacionesFragment extends Fragment {

    private RecyclerView recyclerNotificaciones;
    private List<Notificacion> notificaciones;
    private NotificacionesAdapter notificacionesAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notificaciones, container, false);

        // Inicializar vistas del layout
        recyclerNotificaciones = view.findViewById(R.id.recycler_notificaciones);

        // Configurar RecyclerView
        recyclerNotificaciones.setLayoutManager(new LinearLayoutManager(getContext()));
        notificacionesAdapter = new NotificacionesAdapter();
        recyclerNotificaciones.setAdapter(notificacionesAdapter);

        // Cargar notificaciones del usuario logueado
        cargarNotificaciones();

        return view;
    }

    private void cargarNotificaciones() {

        UsuariosSession userSession = new UsuariosSession(getContext());
        Usuario usuario = userSession.getUser();
        String nroDocumento = String.valueOf(usuario.getNro_documento());

        Log.d("NotificacionesFragment", "Número de documento del usuario: " + nroDocumento);

        DataNotificaciones dataNotificaciones = new DataNotificaciones(getContext());
        dataNotificaciones.obtenerNotificacionesPorUsuario(nroDocumento, notificaciones -> {
            this.notificaciones = notificaciones;
            notificacionesAdapter.setNotificaciones(notificaciones);  // Actualizar el adaptador
        });
    }

}
