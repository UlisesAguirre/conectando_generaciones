package com.example.tpint_grupo2.conexion;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tpint_grupo2.ActividadAgendaAdapter;
import com.example.tpint_grupo2.R;
import com.example.tpint_grupo2.SolicitudAdapter;
import com.example.tpint_grupo2.entidades.Actividad;
import com.example.tpint_grupo2.entidades.ActividadCoordinada;
import com.example.tpint_grupo2.entidades.Usuario;
import com.example.tpint_grupo2.enums.EnumEstadoPresenciaUsuario;
import com.example.tpint_grupo2.enums.EnumEstadosActividades;
import com.example.tpint_grupo2.enums.EnumTiposUsuario;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataMisActividades {

    ArrayList<ActividadCoordinada> listaActividades = new ArrayList<>();
    private RecyclerView rvActividades;
    private ActividadAgendaAdapter actividades;
    private TextView tv_sin_actividades;
    private TextView et_buscar;
    String dniQuery;
    String dniWhere;
    String estadoWhere;



    public void GetAllActividades(View view, Usuario userData) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {


            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL,DataDB.user,DataDB.pass);

                //QUERY xd
                if (userData.getTipo_usuario() == EnumTiposUsuario.VOLUNTARIO) {
                    dniQuery = "ac.dni_usuario_adulto_mayor ";
                    dniWhere = "ac.dni_usuario_voluntario ";
                    estadoWhere = "ac.estado_voluntario";
                } else {
                    dniQuery = "ac.dni_usuario_voluntario " ;
                    dniWhere = "ac.dni_usuario_adulto_mayor ";
                    estadoWhere = "ac.estado_adulto";
                }

                String query = "SELECT * " +
                        "FROM actividadescoordinadas ac " +
                        "LEFT JOIN actividades a ON ac.idActividad = a.idActividad " +
                        "LEFT JOIN usuarios u ON u.nro_documento = " +
                        dniQuery +
                        "WHERE " + estadoWhere + " = \"" + EnumEstadosActividades.ACEPTADA.name() +
                        "\" AND " + dniWhere + "= " + userData.getNro_documento();


                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                while (rs.next()) {
                    ActividadCoordinada actividadCoordinada  = new ActividadCoordinada();
                    actividadCoordinada.setIdActividadCoordinada(rs.getInt(ActividadCoordinada.column_id));

                    Actividad actividad = new Actividad();
                    actividad.setTitulo(rs.getString(Actividad.column_titulo));

                    actividadCoordinada.setActividad(actividad);

                    Usuario usuario = new Usuario();
                    usuario.setNombre(rs.getString(Usuario.column_nombre));
                    usuario.setApellido(rs.getString(Usuario.column_apellido));

                    if (userData.getTipo_usuario() == EnumTiposUsuario.VOLUNTARIO)
                    {
                        usuario.setTipo_usuario(EnumTiposUsuario.ADULTO_MAYOR);
                    } else {
                        usuario.setTipo_usuario(EnumTiposUsuario.VOLUNTARIO);
                    }

                    //Innecesario usar dos usuarios para este caso
                    actividadCoordinada.setVoluntario(usuario);

                    Usuario dataUser = new Usuario();
                    dataUser.setNro_documento(userData.getNro_documento());
                    actividadCoordinada.setAdultoMayor(dataUser);


                    actividadCoordinada.setFechaParticipar(rs.getDate(ActividadCoordinada.column_fecha_a_participar).toString());

                    String estado = rs.getString(ActividadCoordinada.column_estado_voluntario);
                    actividadCoordinada.setEstadoActividad(EnumEstadosActividades.valueOf(estado));


                    listaActividades.add(actividadCoordinada);

                }
                rs.close();
                st.close();
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            new Handler(Looper.getMainLooper()).post(() -> {
                tv_sin_actividades = view.findViewById(R.id.tv_sin_actividades);

                if (listaActividades.size() <= 0) {
                    tv_sin_actividades.setVisibility(View.VISIBLE);
                } else {
                    tv_sin_actividades.setVisibility(View.GONE);
                    rvActividades = view.findViewById(R.id.rvActividades);
                    rvActividades.setLayoutManager(new LinearLayoutManager(view.getContext()));

                    ActividadAgendaAdapter actividadAgendaAdapter = new ActividadAgendaAdapter(listaActividades);
                    rvActividades.setAdapter(actividadAgendaAdapter);
                }


            });

        });

    }

    public void UpdateEstadoActividad(EnumEstadosActividades estado, int idActividad, EnumTiposUsuario tipoUsuario) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);

                if (tipoUsuario == EnumTiposUsuario.VOLUNTARIO) {
                    estadoWhere = "estado_adulto";
                } else {
                    estadoWhere = "estado_voluntario";
                }

                String query = "UPDATE actividadescoordinadas SET " + estadoWhere + " = ? WHERE idActividadCoordinada = ?";
                PreparedStatement pst = con.prepareStatement(query);
                pst.setString(1, estado.name()); // Convertimos el estado a string
                pst.setInt(2, idActividad);


                int rowsAffected = pst.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Solicitud actualizada a " + estado + " para idActividadCoordinada: " + idActividad);
                } else {
                    System.out.println("No se encontró ninguna solicitud con id: " + idActividad);
                }

                pst.close();
                con.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            new Handler(Looper.getMainLooper()).post(() -> {


            });

        });
    }

    public void UpdateEstadoPresenciaUsuario(EnumEstadoPresenciaUsuario estado, int idActividad, int nroDocumento) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);

                String query = "INSERT INTO presencia_usuarios (idActividadCoordinada, nro_documento, presencia) VALUES (?, ?, ?)";

                PreparedStatement pst = con.prepareStatement(query);
                pst.setInt(1, idActividad);
                pst.setInt(2, nroDocumento);
                pst.setString(3, estado.name()); // Convertimos el estado a string

                int rowsAffected = pst.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Registro insertado con presencia: " + estado + " para idActividadCoordinada: " + idActividad);
                } else {
                    System.out.println("No se insertó ningún registro para idActividadCoordinada: " + idActividad);
                }

                pst.close();
                con.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            new Handler(Looper.getMainLooper()).post(() -> {
                // Código para actualizar la interfaz de usuario, si es necesario
            });

        });
    }

}
