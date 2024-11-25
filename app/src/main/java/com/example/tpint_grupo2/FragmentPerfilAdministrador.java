package com.example.tpint_grupo2;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.tpint_grupo2.entidades.Usuario;
import com.example.tpint_grupo2.helpers.SQLite_OpenHelper;
import com.example.tpint_grupo2.sessions.UsuariosSession;

import java.time.LocalDate;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentPerfilAdministrador#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentPerfilAdministrador extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentPerfilAdministrador() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentPerfilAdministrador.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentPerfilAdministrador newInstance(String param1, String param2) {
        FragmentPerfilAdministrador fragment = new FragmentPerfilAdministrador();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private Button btnCerrarSesion;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil_administrador, container, false);

        TextView txtNombreUsuario = view.findViewById(R.id.txt_nombre_usuario);
        TextView txtNacionalidad = view.findViewById(R.id.txt_nacionalidad);
        TextView txtSexo = view.findViewById(R.id.txt_sexo);
        TextView txtFechaNacimiento = view.findViewById(R.id.txt_fecha_nacimiento);
        TextView txtEdad = view.findViewById(R.id.txt_edad);
        TextView txtProvincia = view.findViewById(R.id.txt_provincia);
        TextView txtLocalidad = view.findViewById(R.id.txt_localidad);
        TextView txtDireccion = view.findViewById(R.id.txt_direccion);
        TextView txtTelefono = view.findViewById(R.id.txt_telefono);
        TextView txtEmail = view.findViewById(R.id.txt_email);
        btnCerrarSesion = view.findViewById(R.id.btn_cerrar_sesion);

        // Obtener el usuario de la sesión
        UsuariosSession userSession = new UsuariosSession(getContext());
        Usuario usuario = userSession.getUser();

        // Mostrar los datos
        try {
            txtNombreUsuario.setText(usuario.getNombre() + " " + usuario.getApellido());
            txtNacionalidad.setText(usuario.getNacionalidad().name());
            txtSexo.setText(usuario.getSexo());
            txtFechaNacimiento.setText(usuario.getNacimiento().toString());

            // Calcular edad
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                LocalDate fechaActual = LocalDate.now();
                int edad = fechaActual.getYear() - usuario.getNacimiento().getYear();
                if (fechaActual.getDayOfYear() < usuario.getNacimiento().getDayOfYear()) {
                    edad--;
                }
                txtEdad.setText(String.valueOf(edad));
            }

            txtProvincia.setText(usuario.getLocalidad().getProvincia().getNombre());
            txtLocalidad.setText(usuario.getLocalidad().getNombre());
            txtDireccion.setText(usuario.getDomicilio());
            txtTelefono.setText(usuario.getTelefono());
            txtEmail.setText(usuario.getEmail());

        } catch (Exception e) {
            e.printStackTrace();
        }

        btnCerrarSesion.setOnClickListener(view1 -> {
            // Limpiar sesión en SharedPreferences
            userSession.clearUser();

            // Actualizar el estado del usuario a 0 en la base de datos SQLite
            SQLite_OpenHelper dbHelper = new SQLite_OpenHelper(getActivity());
            dbHelper.abrir();
            dbHelper.actualizarEstadoUsuarioActivo(0);
            dbHelper.cerrar();

            // Redirigir al usuario a la pantalla de inicio de sesión
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        return view;
    }
}