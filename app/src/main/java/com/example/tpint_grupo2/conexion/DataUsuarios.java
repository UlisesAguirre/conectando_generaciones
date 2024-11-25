package com.example.tpint_grupo2.conexion;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.tpint_grupo2.InicioActivity;
import com.example.tpint_grupo2.InteresesActivity;
import com.example.tpint_grupo2.admin_activity;
import com.example.tpint_grupo2.elementos.cargandoDialog;
import com.example.tpint_grupo2.entidades.Localidad;
import com.example.tpint_grupo2.entidades.Provincia;
import com.example.tpint_grupo2.entidades.Usuario;
import com.example.tpint_grupo2.enums.EnumNacionalidades;
import com.example.tpint_grupo2.enums.EnumTiposUsuario;
import com.example.tpint_grupo2.helpers.SQLite_OpenHelper;
import com.example.tpint_grupo2.sessions.UsuariosSession;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.util.Log;

public class DataUsuarios {

    private Context context;
    private ExecutorService executorService;
    private DataLocalidades dataLocalidades;

    public DataUsuarios(Context context) {
        this.context = context;
        this.executorService = Executors.newSingleThreadExecutor();
        this.dataLocalidades = new DataLocalidades(context);
    }

    public void checkUserExists(String nroDocumento, String contrasena) {
        executorService.execute(() -> {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);

                String query = "SELECT * FROM usuarios " +
                        "INNER JOIN localidades lo ON lo.idlocalidad=usuarios.id_localidad " +
                        "INNER JOIN provincias pro ON pro.idProvincias=lo.idProvincia " +
                        "WHERE nro_documento = '" + nroDocumento + "' AND password = '" + contrasena + "'";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                if (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setNro_documento(rs.getInt(Usuario.column_id));
                    usuario.setTipo_usuario(EnumTiposUsuario.valueOf(rs.getString(Usuario.column_tipo_usuario).toUpperCase()));
                    usuario.setPassword(rs.getString(Usuario.column_contrasena));
                    usuario.setNombre(rs.getString(Usuario.column_nombre));
                    usuario.setApellido(rs.getString(Usuario.column_apellido));
                    usuario.setNacionalidad(EnumNacionalidades.valueOf(rs.getString(Usuario.column_nacionalidad).toUpperCase()));
                    usuario.setSexo(rs.getString(Usuario.column_sexo));
                    Date sqlDate = rs.getDate(Usuario.column_nacimiento);
                    LocalDate nacimiento = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        nacimiento = sqlDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    }
                    usuario.setNacimiento(nacimiento);
                    usuario.setDomicilio(rs.getString(Usuario.column_domicilio));
                    usuario.setTelefono(rs.getString(Usuario.column_telefono));
                    usuario.setEmail(rs.getString(Usuario.column_email));
                    usuario.setInstitucion(rs.getString(Usuario.column_institucion));
                    int idLocalidad = rs.getInt(Usuario.column_localidad);
                    Provincia provincia = new Provincia(rs.getInt(Provincia.column_id), rs.getString("pro." + Provincia.column_nombre));
                    Localidad localidad = new Localidad(idLocalidad, provincia, rs.getString("lo." + Localidad.column_nombre));
                    usuario.setLocalidad(localidad);

                    // Verificar intereses
                    String queryIntereses = "SELECT COUNT(*) AS CANTIDAD_REGISTROS FROM intereses " +
                            "WHERE nro_documento= " + nroDocumento;
                    ResultSet rs2 = st.executeQuery(queryIntereses);

                    int cantidad_registros = 0;
                    while (rs2.next()) {
                        cantidad_registros = rs2.getInt("CANTIDAD_REGISTROS");
                    }
                    int intereses = cantidad_registros;

                    // Guardar en la base de datos local
                    SQLite_OpenHelper dbHelper = new SQLite_OpenHelper(context);
                    dbHelper.abrir();
                    if (!dbHelper.validarUsuarioLocal(usuario.getNro_documento(), usuario.getPassword())) {
                        dbHelper.insertarUsuario(usuario.getNro_documento(), usuario.getPassword(), true);
                    } else {
                        dbHelper.actualizarEstadoConexion(usuario.getNro_documento(), true);
                    }
                    dbHelper.cerrar();

                    // Guardar en la sesión
                    try {
                        UsuariosSession userSession = new UsuariosSession(context);
                        userSession.saveUser(usuario);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    rs2.close();

                    new Handler(Looper.getMainLooper()).post(() -> {
                        Intent intent;
                        if (usuario.getTipo_usuario() == EnumTiposUsuario.ADMINISTRADOR) {
                            intent = new Intent(context, admin_activity.class);
                        } else {
                            if (intereses != 0) {
                                intent = new Intent(context, InicioActivity.class);
                            } else {
                                intent = new Intent(context, InteresesActivity.class);
                            }
                        }
                        context.startActivity(intent);
                    });

                } else {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        Toast.makeText(context, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    });
                }

                rs.close();
                st.close();
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(context, "Error al conectar con la base de datos", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    public void registrarse(String nombre, String apellido, String documento, String nacionalidad,
                            String sexo, String fechaNacimiento, String telefono, String institucion,
                            int idLocalidad, String domicilio, String email, String contrasena,
                            String tipoUsuario, RegistrationCallback callback) {
        executorService.execute(() -> {
            boolean success = false;
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);

                String query = "INSERT INTO Usuarios (nombre, apellido, nro_documento, nacionalidad, sexo, " +
                        "nro_telefono, fecha_nacimiento, id_Localidad, domicilio, email, " +
                        "password, tipo_usuario, institucion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, nombre);
                ps.setString(2, apellido);
                ps.setString(3, documento);
                ps.setString(4, nacionalidad);
                ps.setString(5, sexo);
                ps.setString(6, telefono);
                ps.setString(7, fechaNacimiento);
                ps.setInt(8, idLocalidad);
                ps.setString(9, domicilio);
                ps.setString(10, email);
                ps.setString(11, contrasena);
                ps.setString(12, tipoUsuario);
                ps.setString(13, institucion);

                ps.executeUpdate();
                success = true;

                ps.close();
                con.close();
            } catch (Exception e) {
                Log.e("DataUsuarios", "Error during registration: ", e);
            }

            boolean finalSuccess = success;
            new Handler(Looper.getMainLooper()).post(() -> {
                if (finalSuccess) {
                    Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show();
                    Usuario usuario = new Usuario();
                    usuario.setNombre(nombre);
                    usuario.setApellido(apellido);
                    usuario.setNro_documento(Integer.parseInt(documento));
                    usuario.setNacionalidad(EnumNacionalidades.valueOf(nacionalidad.toUpperCase()));
                    usuario.setSexo(sexo);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
                        try{
                            usuario.setNacimiento(LocalDate.parse(fechaNacimiento,formatter));
                        }catch (DateTimeParseException e){
                            System.out.println("Error al parsear la fecha: " + e.getMessage());
                        }
                    }
                    usuario.setTelefono(telefono);
                    usuario.setDomicilio(domicilio);

                    Localidad loc=new Localidad();
                    loc.setIdLocalidad(idLocalidad);

                    usuario.setLocalidad(loc);
                    usuario.setEmail(email);
                    usuario.setPassword(contrasena);
                    usuario.setTipo_usuario(EnumTiposUsuario.VOLUNTARIO);
                    usuario.setInstitucion("");

                    UsuariosSession userSession = new UsuariosSession(context);
                    userSession.saveUser(usuario);

                } else {
                    Toast.makeText(context, "Error al registrar usuario", Toast.LENGTH_SHORT).show();
                }
                callback.onRegistrationResult(finalSuccess);
            });
        });
    }

    public interface RegistrationCallback {
        void onRegistrationResult(boolean success);
    }


    public void obtenerUsuarioPorDocumento(String nroDocumento) {
        executorService.execute(() -> {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);

                String query = "SELECT * FROM usuarios WHERE nro_documento = '" + nroDocumento + "'";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                if (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setNro_documento(rs.getInt(Usuario.column_id));
                    usuario.setTipo_usuario(EnumTiposUsuario.valueOf(rs.getString(Usuario.column_tipo_usuario).toUpperCase()));
                    usuario.setPassword(rs.getString(Usuario.column_contrasena));
                    usuario.setNombre(rs.getString(Usuario.column_nombre));
                    usuario.setApellido(rs.getString(Usuario.column_apellido));
                    usuario.setNacionalidad(EnumNacionalidades.valueOf(rs.getString(Usuario.column_nacionalidad).toUpperCase()));
                    usuario.setSexo(rs.getString(Usuario.column_sexo));
                    Date sqlDate = rs.getDate(Usuario.column_nacimiento);
                    LocalDate nacimiento = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        nacimiento = sqlDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    }
                    usuario.setNacimiento(nacimiento);
                    usuario.setDomicilio(rs.getString(Usuario.column_domicilio));
                    usuario.setTelefono(rs.getString(Usuario.column_telefono));
                    usuario.setEmail(rs.getString(Usuario.column_email));
                    usuario.setInstitucion(rs.getString(Usuario.column_institucion));
                    int idLocalidad = rs.getInt(Usuario.column_localidad);

                    dataLocalidades.obtenerLocalidadPorId(idLocalidad, localidad -> {
                        usuario.setLocalidad(localidad);

                        UsuariosSession userSession = new UsuariosSession(context);
                        userSession.saveUser(usuario);
                    });
                }

                rs.close();
                st.close();
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(context, "Error al conectar con la base de datos", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}