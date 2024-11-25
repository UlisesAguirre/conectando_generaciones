package com.example.tpint_grupo2.conexion;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.tpint_grupo2.entidades.PerfilCompatible;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataPerfilCompatible {

    private Context context;
    private ExecutorService executorService;

    public DataPerfilCompatible(Context context) {
        this.context = context;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public interface PerfilesCompatiblesCallback {
        void onPerfilesCompatiblesCargados(List<PerfilCompatible> perfiles);
    }

    public void obtenerPerfilesCompatibles(String nroDocumento, PerfilesCompatiblesCallback callback) {
        Log.d("DataPerfilCompatible", "Número de documento recibido: " + nroDocumento);

        executorService.execute(() -> {
            List<PerfilCompatible> perfilesCompatibles = new ArrayList<>();
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);

                // Consulta SQL genérica con parámetros, ahora con DISTINCT en GROUP_CONCAT
                String query = "SELECT u.nombre, u.nro_documento, u.apellido, u.email, u.sexo, u.tipo_usuario, " +
                        "GROUP_CONCAT(DISTINCT a.titulo SEPARATOR ', ') AS titulos_actividades, " +  // Agregado DISTINCT para evitar duplicados
                        "i.dias_disponibles, i.horarios_disponibles, " +
                        "CASE " +
                        "   WHEN EXISTS ( " +
                        "       SELECT 1 " +
                        "       FROM ( " +
                        "           SELECT REPLACE(SUBSTRING_INDEX(SUBSTRING_INDEX(i2.dias_disponibles, '-', n), '-', -1), '-', '') AS dia " +
                        "           FROM ( " +
                        "               SELECT '1' AS n UNION SELECT '2' UNION SELECT '3' UNION SELECT '4' UNION SELECT '5' UNION SELECT '6' UNION SELECT '7' " +
                        "           ) AS numbers " +
                        "           JOIN intereses i2 ON i2.nro_documento = ? " +  // Parametriza el nro_documento de comparación
                        "           WHERE CHAR_LENGTH(i2.dias_disponibles) " +
                        "                 - CHAR_LENGTH(REPLACE(i2.dias_disponibles, '-', '')) >= n - 1 " +
                        "       ) AS dias_comparacion " +
                        "       WHERE i.dias_disponibles LIKE CONCAT('%', dia, '%') " +
                        "   ) " +
                        "   THEN 1 " +
                        "   ELSE 0 " +
                        "END AS resultado_comparacion " +
                        "FROM usuarios u " +
                        "JOIN intereses i ON u.nro_documento = i.nro_documento " +
                        "JOIN actividadespreferidas ap ON ap.idInteres = i.idIntereses " +
                        "JOIN categorias c ON c.idCategoria = ap.idCategoria " +
                        "JOIN actividades a ON a.idCategoria = c.idCategoria " +
                        "JOIN localidades l ON u.id_localidad = l.idLocalidad " +
                        "JOIN provincias p ON l.idProvincia = p.idProvincias " +
                        "WHERE a.idActividad IN ( " +
                        "   SELECT a_sub.idActividad " +
                        "   FROM usuarios u_sub " +
                        "   JOIN intereses i_sub ON u_sub.nro_documento = i_sub.nro_documento " +
                        "   JOIN actividadespreferidas ap_sub ON ap_sub.idInteres = i_sub.idIntereses " +
                        "   JOIN categorias c_sub ON c_sub.idCategoria = ap_sub.idCategoria " +
                        "   JOIN actividades a_sub ON a_sub.idCategoria = c_sub.idCategoria " +
                        "   WHERE u_sub.nro_documento = ? " +  // Parametriza el nro_documento de comparación
                        ") " +
                        "AND u.nro_documento <> ? " +  // Excluye el mismo nro_documento
                        "AND u.tipo_usuario <> ( " +
                        "   SELECT tipo_usuario " +
                        "   FROM usuarios " +
                        "   WHERE nro_documento = ? " +  // Parametriza el tipo_usuario de comparación
                        ") " +
                        "AND p.nombre = ( " +  // Compara la provincia
                        "   SELECT p_sub.nombre " +
                        "   FROM usuarios u_sub " +
                        "   JOIN localidades l_sub ON u_sub.id_localidad = l_sub.idLocalidad " +
                        "   JOIN provincias p_sub ON l_sub.idProvincia = p_sub.idProvincias " +
                        "   WHERE u_sub.nro_documento = ? " +  // Parametriza el nro_documento de comparación para la provincia
                        ") " +
                        "GROUP BY u.nro_documento, u.nombre, u.apellido, u.email, u.sexo, u.tipo_usuario, " +
                        "i.dias_disponibles, i.horarios_disponibles " +
                        "HAVING resultado_comparacion = 1;";

                Log.d("DataPerfilCompatible", "Consulta SQL: " + query);

                PreparedStatement preparedStatement = con.prepareStatement(query);
                preparedStatement.setString(1, nroDocumento);
                preparedStatement.setString(2, nroDocumento);
                preparedStatement.setString(3, nroDocumento);
                preparedStatement.setString(4, nroDocumento);
                preparedStatement.setString(5, nroDocumento);

                ResultSet rs = preparedStatement.executeQuery();

                while (rs.next()) {
                    PerfilCompatible perfil = new PerfilCompatible();
                    perfil.setNro_documento(rs.getInt("nro_documento"));
                    perfil.setNombre(rs.getString("nombre"));
                    perfil.setApellido(rs.getString("apellido"));
                    perfil.setEmail(rs.getString("email"));
                    perfil.setSexo(rs.getString("sexo"));
                    perfil.setTituloActividad(rs.getString("titulos_actividades")); // Usar el campo concatenado
                    perfil.setDiasDisponibles(rs.getString("dias_disponibles"));
                    perfil.setHorariosDisponibles(rs.getString("horarios_disponibles"));

                    // Log para verificar los datos obtenidos de la base de datos
                    Log.d("DataPerfilCompatible", "Perfil encontrado:");
                    Log.d("DataPerfilCompatible", "Nombre: " + perfil.getNombre() + " " + perfil.getApellido());
                    Log.d("DataPerfilCompatible", "Email: " + perfil.getEmail());
                    Log.d("DataPerfilCompatible", "Sexo: " + perfil.getSexo());
                    Log.d("DataPerfilCompatible", "Actividades preferidas: " + perfil.getTituloActividad());
                    Log.d("DataPerfilCompatible", "Días disponibles: " + perfil.getDiasDisponibles());
                    Log.d("DataPerfilCompatible", "Horarios disponibles: " + perfil.getHorariosDisponibles());

                    perfilesCompatibles.add(perfil);
                }

                rs.close();
                preparedStatement.close();
                con.close();

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (perfilesCompatibles.isEmpty()) {
                        Toast.makeText(context, "No se encontraron perfiles compatibles", Toast.LENGTH_SHORT).show();
                    } else {
                        callback.onPerfilesCompatiblesCargados(perfilesCompatibles);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(context, "Error al obtener los perfiles compatibles", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

}
