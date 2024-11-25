package com.example.tpint_grupo2.conexion;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.tpint_grupo2.entidades.Notificacion;
import com.example.tpint_grupo2.enums.EnumTipoNotificaciones;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataNotificaciones {

    private Context context;
    private ExecutorService executorService;

    public DataNotificaciones(Context context) {
        this.context = context;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public DataNotificaciones(){

    }

    // Interfaz para el callback de las notificaciones cargadas
    public interface NotificacionesCallback {
        void onNotificacionesCargadas(List<Notificacion> notificaciones);
    }

    // Método para obtener las notificaciones del usuario
    public void obtenerNotificacionesPorUsuario(String nroDocumento, NotificacionesCallback callback) {
        Log.d("DataNotificaciones", "Número de documento recibido: " + nroDocumento);

        executorService.execute(() -> {
            List<Notificacion> notificaciones = new ArrayList<>();
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);

                // Actualizamos la consulta SQL para incluir el filtro de estado y ordenar por fecha
                String query = "SELECT n.idNotificaciones, n.tipo_notificacion, n.estado, n.fecha_envio, u.nombre, u.apellido " +
                        "FROM notificaciones n " +
                        "JOIN actividadescoordinadas ac ON n.idActividadCoordinada = ac.idactividadCoordinada " +
                        "JOIN usuarios u ON u.nro_documento = n.nro_documento " +
                        "WHERE n.nro_documento_destino = '" + nroDocumento + "' " +
                        "AND n.estado IN ('Pendiente', 'RECIBIDA') " +
                        "ORDER BY n.fecha_envio DESC";  // Ordenamos por fecha de envío en orden descendente

                Log.d("DataNotificaciones", "Consulta SQL: " + query);
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                while (rs.next()) {
                    Notificacion notificacion = new Notificacion();
                    notificacion.setId(rs.getInt("idNotificaciones"));
                    notificacion.setTipoNotificacion(EnumTipoNotificaciones.valueOf(rs.getString("tipo_notificacion")));
                    notificacion.setEstado(rs.getString("estado"));

                    // Asignar fecha de envío
                    String fechaEnvioString = rs.getString("fecha_envio");
                    String fechaSolo = fechaEnvioString.split(" ")[0];  // Extrae solo la fecha (yyyy-MM-dd)
                    LocalDate fechaEnvio = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        fechaEnvio = LocalDate.parse(fechaSolo);
                    }
                    notificacion.setFecha_envio(fechaEnvio);

                    // Asignar nombreRemitente
                    String nombreRemitente = rs.getString("nombre") + " " + rs.getString("apellido");
                    notificacion.setNombreRemitente(nombreRemitente);

                    notificaciones.add(notificacion);
                }

                rs.close();
                st.close();
                con.close();

                // Notificar al callback en el hilo principal
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (notificaciones.isEmpty()) {
                        Toast.makeText(context, "No tienes notificaciones", Toast.LENGTH_SHORT).show();
                    } else {
                        callback.onNotificacionesCargadas(notificaciones);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(context, "Error al obtener las notificaciones", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }


    public void insertarNotificacion(Notificacion noti) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            int filasAfectadas = 0;
            try {
                // Cargar el driver y establecer la conexión
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();

                // Obtener el estado y concatenar "OK"
                String estadoConOK = noti.getEstado() + " OK";

                // Actualizar el estado de la notificación con el nuevo valor
                int filasActualizadas = st.executeUpdate("UPDATE notificaciones " +
                        "SET estado = '" + estadoConOK + "' " +
                        "WHERE idActividadCoordinada = '" + noti.getActividadCoordinada().getIdActividadCoordinada() + "';");



                // Insertar la notificación
                filasAfectadas = st.executeUpdate("INSERT INTO notificaciones " +
                        "(nro_documento, tipo_notificacion, fecha_envio, estado, idActividadCoordinada, " +
                        "nro_documento_destino) VALUES ('" + noti.getUsuario().getNro_documento() +
                        "', '" + noti.getTipoNotificacion().toString() + "', now(), '" + noti.getEstado() + "', " +
                        "'" + noti.getActividadCoordinada().getIdActividadCoordinada() + "', '" + noti.getUsuarioDestino().getNro_documento() + "');");

                // Cerrar conexiones
                st.close();
                con.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            int filas = filasAfectadas;
            // Volver al hilo principal
            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                // Comprobar si hubo error al insertar o actualizar
                if(filas <= 0){
                    System.err.println("Error al enviar notificación o actualizar estado.");
                }
            });
        });
    }

    // Método para actualizar el estado de la notificación a "Eliminada"
    public void actualizarEstadoNotificacion(Notificacion notificacion) {
        executorService.execute(() -> {
            try {
                // Establecer la conexión con la base de datos
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();

                // Actualizar el estado de la notificación a "Eliminada"
                String query = "UPDATE notificaciones SET estado = 'Eliminada' WHERE idNotificaciones = " + notificacion.getId();
                int filasAfectadas = st.executeUpdate(query);

                if (filasAfectadas > 0) {
                    // Si la actualización fue exitosa, enviar un mensaje al usuario
                    new Handler(Looper.getMainLooper()).post(() -> {
                        Toast.makeText(context, "Notificación eliminada correctamente", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    // Si no se afectaron filas, significa que algo salió mal
                    new Handler(Looper.getMainLooper()).post(() -> {
                        Toast.makeText(context, "Error al eliminar la notificación", Toast.LENGTH_SHORT).show();
                    });
                }

                // Cerrar conexiones
                st.close();
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(context, "Error al actualizar el estado", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

}
