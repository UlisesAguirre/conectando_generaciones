package com.example.tpint_grupo2.sessions;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.tpint_grupo2.entidades.PerfilCompatible;

public class UsuariosCompatiblesSession {

    private static final String PREF_NAME = "compatible_user_session";
    private static final String KEY_NRO_DOCUMENTO = "nro_documento"; // Nueva clave para nro_documento
    private static final String KEY_NOMBRE = "nombre";
    private static final String KEY_APELLIDO = "apellido";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_SEXO = "sexo";
    private static final String KEY_TITULO_ACTIVIDAD = "titulo_actividad";
    private static final String KEY_SELECCIONADO = "seleccionado";

    // Nuevas claves para días y horarios disponibles
    private static final String KEY_DIAS_DISPONIBLES = "dias_disponibles";
    private static final String KEY_HORARIOS_DISPONIBLES = "horarios_disponibles";

    private SharedPreferences sharedPreferences;

    public UsuariosCompatiblesSession(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Método para guardar un PerfilCompatible en la sesión
    public void savePerfilCompatible(PerfilCompatible perfil) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_NRO_DOCUMENTO, perfil.getNro_documento()); // Guardar el nro_documento
        editor.putString(KEY_NOMBRE, perfil.getNombre());
        editor.putString(KEY_APELLIDO, perfil.getApellido());
        editor.putString(KEY_EMAIL, perfil.getEmail());
        editor.putString(KEY_SEXO, perfil.getSexo());
        editor.putString(KEY_TITULO_ACTIVIDAD, perfil.getTituloActividad());
        editor.putBoolean(KEY_SELECCIONADO, perfil.isSeleccionado());

        // Guardar los nuevos valores de días y horarios disponibles
        editor.putString(KEY_DIAS_DISPONIBLES, perfil.getDiasDisponibles());
        editor.putString(KEY_HORARIOS_DISPONIBLES, perfil.getHorariosDisponibles());

        editor.apply();
    }

    // Método para obtener el PerfilCompatible de la sesión
    public PerfilCompatible getPerfilCompatible() {
        int nro_documento = sharedPreferences.getInt(KEY_NRO_DOCUMENTO, -1); // Obtener el nro_documento
        String nombre = sharedPreferences.getString(KEY_NOMBRE, null);
        String apellido = sharedPreferences.getString(KEY_APELLIDO, null);
        String email = sharedPreferences.getString(KEY_EMAIL, null);
        String sexo = sharedPreferences.getString(KEY_SEXO, null);
        String tituloActividad = sharedPreferences.getString(KEY_TITULO_ACTIVIDAD, null);
        boolean seleccionado = sharedPreferences.getBoolean(KEY_SELECCIONADO, false);

        // Recuperar los nuevos valores de días y horarios disponibles
        String diasDisponibles = sharedPreferences.getString(KEY_DIAS_DISPONIBLES, "No disponible");
        String horariosDisponibles = sharedPreferences.getString(KEY_HORARIOS_DISPONIBLES, "No disponible");

        if (nombre == null || apellido == null || email == null || sexo == null || tituloActividad == null) {
            return null; // No hay datos almacenados
        }

        PerfilCompatible perfil = new PerfilCompatible();
        perfil.setNro_documento(nro_documento); // Establecer el nro_documento
        perfil.setNombre(nombre);
        perfil.setApellido(apellido);
        perfil.setEmail(email);
        perfil.setSexo(sexo);
        perfil.setTituloActividad(tituloActividad);
        perfil.setSeleccionado(seleccionado);

        // Establecer los nuevos valores de días y horarios
        perfil.setDiasDisponibles(diasDisponibles);
        perfil.setHorariosDisponibles(horariosDisponibles);

        return perfil;
    }

    // Método para limpiar el PerfilCompatible de la sesión
    public void clearPerfilCompatible() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
