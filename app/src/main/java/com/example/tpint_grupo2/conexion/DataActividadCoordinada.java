package com.example.tpint_grupo2.conexion;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.tpint_grupo2.entidades.ActividadCoordinada;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataActividadCoordinada {

    private Context context;
    private ExecutorService executorService;

    public DataActividadCoordinada(Context context) {
        this.context = context;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    // Método para insertar la ActividadCoordinada en la base de datos
    public void insertarActividadCoordinada(ActividadCoordinada actividadCoordinada) {
        Log.d("DataActividadCoordinada", "Iniciando inserción de actividad coordinada");

        executorService.execute(() -> {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);

                // Verificar si ya existe una actividad con los mismos datos
                if (actividadExistente(con, actividadCoordinada)) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        Toast.makeText(context, "Ya existe una actividad coordinada con estos datos.", Toast.LENGTH_SHORT).show();
                    });
                    return; // Salir si ya existe la actividad
                }

                // Consulta SQL para insertar la actividad coordinada
                String queryActividad = "INSERT INTO " + ActividadCoordinada.NOMBRE_TABLA + " (" +
                        ActividadCoordinada.column_id_actividad + ", " +
                        ActividadCoordinada.column_dni_voluntario + ", " +
                        ActividadCoordinada.column_dni_adulto_mayor + ", " +
                        ActividadCoordinada.column_fecha_a_participar + ", " +
                        ActividadCoordinada.column_fecha_solicitud + ", " +
                        ActividadCoordinada.column_horario_inicio + ", " +
                        ActividadCoordinada.column_estado_voluntario + ", " +
                        ActividadCoordinada.column_estado_adulto + ") " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                PreparedStatement preparedStatementActividad = con.prepareStatement(queryActividad, PreparedStatement.RETURN_GENERATED_KEYS);
                preparedStatementActividad.setInt(1, actividadCoordinada.getIdActividadBD());
                preparedStatementActividad.setInt(2, actividadCoordinada.getDniVoluntarioBD());
                preparedStatementActividad.setInt(3, actividadCoordinada.getDniAdultoMayorBD());
                preparedStatementActividad.setString(4, actividadCoordinada.getFechaParticipar());
                preparedStatementActividad.setString(5, actividadCoordinada.getFechaSolicitud());
                preparedStatementActividad.setString(6, actividadCoordinada.getHorario());
                preparedStatementActividad.setString(7, actividadCoordinada.getEstadoVoluntarioBD());
                preparedStatementActividad.setString(8, actividadCoordinada.getEstadoAdultoBD());

                int rowsAffectedActividad = preparedStatementActividad.executeUpdate();

                if (rowsAffectedActividad > 0) {
                    // Obtener el ID generado automáticamente para la actividad coordinada
                    ResultSet generatedKeys = preparedStatementActividad.getGeneratedKeys();
                    int idActividadCoordinada = 0;
                    if (generatedKeys.next()) {
                        idActividadCoordinada = generatedKeys.getInt(1);
                    }

                    // Consulta SQL para insertar la notificación
                    String queryNotificacion = "INSERT INTO notificaciones (nro_documento, tipo_notificacion, fecha_envio, estado, idActividadCoordinada, nro_documento_destino) " +
                            "VALUES (?, ?, ?, ?, ?, ?)";

                    PreparedStatement preparedStatementNotificacion = con.prepareStatement(queryNotificacion);
                    preparedStatementNotificacion.setInt(1, actividadCoordinada.getDniVoluntarioBD()); // Documento del remitente
                    preparedStatementNotificacion.setString(2, "Invitacion");
                    preparedStatementNotificacion.setString(3, actividadCoordinada.getFechaSolicitud()); // Fecha de envío
                    preparedStatementNotificacion.setString(4, "Pendiente"); // Estado de la notificación
                    preparedStatementNotificacion.setInt(5, idActividadCoordinada); // ID de la actividad coordinada
                    preparedStatementNotificacion.setInt(6, actividadCoordinada.getDniAdultoMayorBD()); // Documento de destino

                    int rowsAffectedNotificacion = preparedStatementNotificacion.executeUpdate();
                    preparedStatementNotificacion.close();
                }

                preparedStatementActividad.close();
                con.close();

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (rowsAffectedActividad > 0) {
                        Toast.makeText(context, "Actividad coordinada y notificación insertadas correctamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Error al insertar la actividad coordinada", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(context, "Error en la inserción de la actividad coordinada", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }


    // Método para verificar si ya existe una actividad coordinada con los mismos datos
    private boolean actividadExistente(Connection con, ActividadCoordinada actividadCoordinada) {
        try {
            String query = "SELECT COUNT(*) FROM " + ActividadCoordinada.NOMBRE_TABLA + " WHERE " +
                    ActividadCoordinada.column_fecha_a_participar + " = ? AND " +
                    ActividadCoordinada.column_id_actividad + " = ? AND " +
                    ActividadCoordinada.column_horario_inicio + " = ? AND " +
                    ActividadCoordinada.column_dni_voluntario + " = ? AND " +
                    ActividadCoordinada.column_dni_adulto_mayor + " = ?";

            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, actividadCoordinada.getFechaParticipar());
            preparedStatement.setInt(2, actividadCoordinada.getIdActividadBD());
            preparedStatement.setString(3, actividadCoordinada.getHorario());
            preparedStatement.setInt(4, actividadCoordinada.getDniVoluntarioBD());
            preparedStatement.setInt(5, actividadCoordinada.getDniAdultoMayorBD());

            // Ejecutar la consulta
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                resultSet.close();
                preparedStatement.close();
                return count > 0; // Si el conteo es mayor que 0, ya existe la actividad
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Si hubo un error o no hay coincidencias, retornar false
    }
}
