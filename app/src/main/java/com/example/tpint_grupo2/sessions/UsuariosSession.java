package com.example.tpint_grupo2.sessions;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.example.tpint_grupo2.entidades.Usuario;
import com.example.tpint_grupo2.entidades.Localidad;
import com.example.tpint_grupo2.entidades.Provincia;
import com.example.tpint_grupo2.enums.EnumNacionalidades;
import com.example.tpint_grupo2.enums.EnumTiposUsuario;

import java.time.LocalDate;

public class UsuariosSession {

    private static final String PREF_NAME = "user_session";
    private static final String KEY_NRO_DOCUMENTO = "nro_documento";
    private static final String KEY_NOMBRE = "nombre";
    private static final String KEY_APELLIDO = "apellido";
    private static final String KEY_NACIONALIDAD = "nacionalidad";
    private static final String KEY_SEXO = "sexo";
    private static final String KEY_FECHA_NACIMIENTO = "fecha_nacimiento";
    private static final String KEY_TELEFONO = "telefono";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_LOCALIDAD_ID = "localidad_id";
    private static final String KEY_LOCALIDAD_NOMBRE = "localidad_nombre";
    private static final String KEY_PROVINCIA_ID = "provincia_id";
    private static final String KEY_PROVINCIA_NOMBRE = "provincia_nombre";
    private static final String KEY_DOMICILIO = "domicilio";
    private static final String KEY_TIPO_USUARIO = "tipo_usuario";

    private SharedPreferences sharedPreferences;

    public UsuariosSession(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveUser(Usuario usuario) {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_NOMBRE, usuario.getNombre());
            editor.putInt(KEY_NRO_DOCUMENTO, usuario.getNro_documento());
            editor.putString(KEY_APELLIDO, usuario.getApellido());
            editor.putString(KEY_NACIONALIDAD, usuario.getNacionalidad().name());
            editor.putString(KEY_SEXO, usuario.getSexo());
            editor.putString(KEY_FECHA_NACIMIENTO, usuario.getNacimiento().toString());
            editor.putString(KEY_TELEFONO, usuario.getTelefono());
            editor.putString(KEY_EMAIL, usuario.getEmail());
            editor.putString(KEY_DOMICILIO, usuario.getDomicilio());

            if (usuario.getLocalidad() != null) {
                editor.putInt(KEY_LOCALIDAD_ID, usuario.getLocalidad().getIdLocalidad());
                editor.putString(KEY_LOCALIDAD_NOMBRE, usuario.getLocalidad().getNombre());
                if (usuario.getLocalidad().getProvincia() != null) {
                    editor.putInt(KEY_PROVINCIA_ID, usuario.getLocalidad().getProvincia().getIdProvincia());
                    editor.putString(KEY_PROVINCIA_NOMBRE, usuario.getLocalidad().getProvincia().getNombre());
                }
            }

            editor.putString(KEY_TIPO_USUARIO, usuario.getTipo_usuario().name());
            editor.apply();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Usuario getUser() {
        Usuario usuario=null;
        try {
            String nombre = sharedPreferences.getString(KEY_NOMBRE, "");
            int nroDocumento = sharedPreferences.getInt(KEY_NRO_DOCUMENTO, -1);
            String apellido = sharedPreferences.getString(KEY_APELLIDO, "");
            String nacionalidad = sharedPreferences.getString(KEY_NACIONALIDAD, "");
            String sexo = sharedPreferences.getString(KEY_SEXO, "");
            String fechaNacimiento = sharedPreferences.getString(KEY_FECHA_NACIMIENTO, "");
            String telefono = sharedPreferences.getString(KEY_TELEFONO, "");
            String email = sharedPreferences.getString(KEY_EMAIL, "");
            String domicilio = sharedPreferences.getString(KEY_DOMICILIO, "");

            int localidadId = sharedPreferences.getInt(KEY_LOCALIDAD_ID, -1);
            String localidadNombre = sharedPreferences.getString(KEY_LOCALIDAD_NOMBRE, "");

            int provinciaId = sharedPreferences.getInt(KEY_PROVINCIA_ID, -1);
            String provinciaNombre = sharedPreferences.getString(KEY_PROVINCIA_NOMBRE, "");

            Provincia provincia = new Provincia(provinciaId, provinciaNombre);
            Localidad localidad = new Localidad(localidadId, provincia, localidadNombre);

            String tipoUsuario = sharedPreferences.getString(KEY_TIPO_USUARIO, "");

            usuario = new Usuario();
            usuario.setNro_documento(nroDocumento);
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setNacionalidad(EnumNacionalidades.valueOf(nacionalidad));
            usuario.setSexo(sexo);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                usuario.setNacimiento(LocalDate.parse(fechaNacimiento));
            }
            usuario.setTelefono(telefono);
            usuario.setEmail(email);
            usuario.setDomicilio(domicilio);
            usuario.setLocalidad(localidad);
            usuario.setTipo_usuario(EnumTiposUsuario.valueOf(tipoUsuario));
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return usuario;
        }

    }

    public void clearUser() {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}