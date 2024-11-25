package com.example.tpint_grupo2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tpint_grupo2.elementos.cargandoDialog;
import com.example.tpint_grupo2.entidades.Usuario;
import com.example.tpint_grupo2.enums.EnumTiposUsuario;
import com.example.tpint_grupo2.sessions.UsuariosSession;
import com.example.tpint_grupo2.helpers.SQLite_OpenHelper;

import java.time.LocalDate;

public class MiPerfilFragment extends Fragment {

    private TextView txtNombreUsuario, txtNacionalidad, txtSexo, txtFechaNacimiento,
            txtEdad, txtProvincia, txtLocalidad, txtDireccion, txtTelefono, txtEmail;
    private ImageView imgPerfil;
    private Button btnCerrarSesion;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_miperfil, container, false);

        // Obtener los datos del usuario de la sesión
        UsuariosSession userSession = new UsuariosSession(getContext());
        Usuario usuario = userSession.getUser();

        // Inicializar los componentes
        txtNombreUsuario = view.findViewById(R.id.txt_nombre_usuario);
        txtNacionalidad = view.findViewById(R.id.txt_nacionalidad);
        txtSexo = view.findViewById(R.id.txt_sexo);
        txtFechaNacimiento = view.findViewById(R.id.txt_fecha_nacimiento);
        txtEdad = view.findViewById(R.id.txt_edad);
        txtProvincia = view.findViewById(R.id.txt_provincia);
        txtLocalidad = view.findViewById(R.id.txt_localidad);
        txtDireccion = view.findViewById(R.id.txt_direccion);
        txtTelefono = view.findViewById(R.id.txt_telefono);
        txtEmail = view.findViewById(R.id.txt_email);
        imgPerfil = view.findViewById(R.id.img_perfil);
        btnCerrarSesion = view.findViewById(R.id.btn_cerrar_sesion);
        Button btnModificarIntereses = view.findViewById(R.id.btn_modificar_intereses); // Referencia al botón

        // Mostrar los datos inmediatamente
        try {
            txtNombreUsuario.append(usuario.getNombre() + " " + usuario.getApellido());
            txtNacionalidad.append(" " + usuario.getNacionalidad().name());

            // Modificar la visualización del sexo
            String sexo = usuario.getSexo();
            if ("F".equals(sexo)) {
                txtSexo.append(" Femenino");
            } else if ("M".equals(sexo)) {
                txtSexo.append(" Masculino");
            } else {
                txtSexo.append(" " + sexo); // En caso de que sea otro valor
            }

            txtFechaNacimiento.append(" " + usuario.getNacimiento().toString());

            // Calcular la edad
            int edad = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                LocalDate fechaActual = LocalDate.now();
                edad = fechaActual.getYear() - usuario.getNacimiento().getYear();
                if (fechaActual.getDayOfYear() < usuario.getNacimiento().getDayOfYear()) {
                    edad--;
                }
            }

            txtEdad.append(" " + String.valueOf(edad));
            txtTelefono.append(" " + usuario.getTelefono());
            txtEmail.append(" " + usuario.getEmail());
            txtDireccion.append(" " + usuario.getDomicilio());
            txtProvincia.append(" " + usuario.getLocalidad().getProvincia().getNombre());
            txtLocalidad.append(" " + usuario.getLocalidad().getNombre());

            if(usuario.getTipo_usuario().equals(EnumTiposUsuario.ADULTO_MAYOR)){
                imgPerfil.setImageResource(R.mipmap.adulto_mayor_icon);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Lógica para cerrar sesión
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

        // Lógica para modificar intereses
        btnModificarIntereses.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), InteresesActivity.class);
            intent.putExtra("isFromModifyButton", true); // Pasa el valor booleano
            startActivity(intent);
        });

        return view;
    }

}