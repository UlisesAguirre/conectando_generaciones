package com.example.tpint_grupo2.conexion;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tpint_grupo2.ActividadPasadaAdapter;
import com.example.tpint_grupo2.R;
import com.example.tpint_grupo2.SolicitudAdapter;
import com.example.tpint_grupo2.entidades.Actividad;
import com.example.tpint_grupo2.entidades.ActividadAgenda;
import com.example.tpint_grupo2.entidades.ActividadCoordinada;
import com.example.tpint_grupo2.entidades.PresenciaUsuarios;
import com.example.tpint_grupo2.entidades.Usuario;
import com.example.tpint_grupo2.enums.EnumEstadosActividades;
import com.example.tpint_grupo2.enums.EnumTiposUsuario;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataHistorialActividades {

    ArrayList<PresenciaUsuarios> historialActividades = new ArrayList<>();
    private RecyclerView rvHistorialActividades;
    private ActividadPasadaAdapter actividadPasadaAdapter;
    private TextView tv_sin_historial_actividades;
    String dniQuery;
    String dniWhere;
    String estadoWhere;


    public void GetAllActividades(View view, Fragment parentFragment, Usuario userData) {
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
                        "LEFT JOIN presencia_usuarios pu ON ac.idActividadCoordinada = pu.idActividadCoordinada " +
                        "LEFT JOIN usuarios u ON u.nro_documento = " +
                        dniQuery +
                        "WHERE " + estadoWhere + " IN (\"" +
                        EnumEstadosActividades.CALIFICADA.name() + "\", \"" +
                        EnumEstadosActividades.NO_CALIFICADA.name() + "\", \"" +
                        EnumEstadosActividades.NO_ASISTIO.name() + "\", \"" +
                        EnumEstadosActividades.RECHAZADA.name() + "\") AND " +
                        dniWhere + "= " + userData.getNro_documento();



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

                    //Innecesario usar dos usuarios para este caso
                    actividadCoordinada.setVoluntario(usuario);



                    actividadCoordinada.setFechaParticipar(rs.getDate(ActividadCoordinada.column_fecha_a_participar).toString());

                    if (userData.getTipo_usuario() == EnumTiposUsuario.VOLUNTARIO) {
                        String estado = rs.getString(ActividadCoordinada.column_estado_voluntario);
                        actividadCoordinada.setEstadoActividad(EnumEstadosActividades.valueOf(estado));
                    } else {
                        String estado = rs.getString(ActividadCoordinada.column_estado_adulto);
                        actividadCoordinada.setEstadoActividad(EnumEstadosActividades.valueOf(estado));
                    }


                    PresenciaUsuarios presenciaUsuarios = new PresenciaUsuarios();
                    presenciaUsuarios.setCalificacion(rs.getInt(PresenciaUsuarios.column_calificacion));
                    presenciaUsuarios.setOpinion(rs.getString(PresenciaUsuarios.column_opinion));
                    presenciaUsuarios.setActividad(actividadCoordinada);

                    historialActividades.add(presenciaUsuarios);

                }
                rs.close();
                st.close();
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            new Handler(Looper.getMainLooper()).post(() -> {
                tv_sin_historial_actividades = view.findViewById(R.id.tv_sin_historial_actividades);

                if (historialActividades.size() <= 0) {
                    tv_sin_historial_actividades.setVisibility(View.VISIBLE);
                } else {
                    tv_sin_historial_actividades.setVisibility(View.GONE);
                    rvHistorialActividades = view.findViewById(R.id.rvHistorialActividades);
                    rvHistorialActividades.setLayoutManager(new LinearLayoutManager(view.getContext()));

                    actividadPasadaAdapter = new ActividadPasadaAdapter(historialActividades, parentFragment, false);
                    rvHistorialActividades.setAdapter(actividadPasadaAdapter);
                }


            });

        });
    }
}
