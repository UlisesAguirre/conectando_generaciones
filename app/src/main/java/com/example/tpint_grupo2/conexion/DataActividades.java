package com.example.tpint_grupo2.conexion;

import android.content.Context;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.tpint_grupo2.entidades.Actividad;
import com.example.tpint_grupo2.entidades.Categoria;
import com.example.tpint_grupo2.enums.EnumDias;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.function.Consumer;

public class DataActividades {

    private Context context;
    private ExecutorService executorService;

    public DataActividades(Context context) {
        this.context = context;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void obtenerActividadesPorUsuario(Consumer<List<Actividad>> callback, int nroDocumento) {
        executorService.execute(() -> {
            List<Actividad> actividades = new ArrayList<>();
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);

                // Consulta actualizada con la nueva lógica
                String query = "SELECT DISTINCT a.*, i.nro_documento, i.dias_disponibles, i.horarios_disponibles " +
                        "FROM actividades a " +
                        "JOIN intereses i ON 1 = 1 " +
                        "WHERE i.nro_documento = ? " +
                        "AND a.estado = 1 " +
                        "AND (" +
                        "  (a.dias_dictados LIKE '%Lunes%' AND i.dias_disponibles LIKE '%Lunes%') OR " +
                        "  (a.dias_dictados LIKE '%Martes%' AND i.dias_disponibles LIKE '%Martes%') OR " +
                        "  (a.dias_dictados LIKE '%Miercoles%' AND i.dias_disponibles LIKE '%Miercoles%') OR " +
                        "  (a.dias_dictados LIKE '%Jueves%' AND i.dias_disponibles LIKE '%Jueves%') OR " +
                        "  (a.dias_dictados LIKE '%Viernes%' AND i.dias_disponibles LIKE '%Viernes%') OR " +
                        "  (a.dias_dictados LIKE '%Sabado%' AND i.dias_disponibles LIKE '%Sabado%') OR " +
                        "  (a.dias_dictados LIKE '%Domingo%' AND i.dias_disponibles LIKE '%Domingo%') OR " +
                        "  (a.dias_dictados LIKE '%LIBRE%')" +
                        ") " +
                        "AND (" +
                        "  (SUBSTRING_INDEX(a.horario, ' - ', 1) >= SUBSTRING_INDEX(i.horarios_disponibles, ' - ', 1) " +
                        "  AND SUBSTRING_INDEX(a.horario, ' - ', -1) <= SUBSTRING_INDEX(i.horarios_disponibles, ' - ', -1)) OR " +
                        "  (a.horario LIKE '%LIBRE%')" +
                        ");";

                // Preparar y ejecutar la consulta
                PreparedStatement ps = con.prepareStatement(query);
                ps.setInt(1, nroDocumento);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    Actividad actividad = new Actividad();
                    actividad.setIdActividad(rs.getInt(Actividad.column_id));
                    actividad.setTitulo(rs.getString(Actividad.column_titulo));
                    actividad.setCategoria(new Categoria(rs.getInt(Actividad.column_categoria_id), "", ""));

                    // Procesar días dictados
                    String diasDictadosString = rs.getString(Actividad.column_dias_dictados);
                    if (diasDictadosString != null && !diasDictadosString.isEmpty()) {
                        ArrayList<EnumDias> diasDictados = new ArrayList<>();
                        String[] diasArray = diasDictadosString.split("-"); // Suponiendo que los días están separados por guiones
                        for (String dia : diasArray) {
                            try {
                                // Convertir a EnumDias usando el valor tal como está en la base de datos
                                diasDictados.add(EnumDias.valueOf(dia.trim()));
                            } catch (IllegalArgumentException e) {
                                System.err.println("Día desconocido: " + dia);
                            }
                        }
                        actividad.setDias_dictados(diasDictados);
                    } else {
                        actividad.setDias_dictados(new ArrayList<>()); // Lista vacía si no hay días
                    }

                    // Agregar la actividad a la lista
                    actividades.add(actividad);
                }

                rs.close();
                ps.close();
                con.close();

                // Devolver resultados al hilo principal
                new Handler(Looper.getMainLooper()).post(() -> callback.accept(actividades));

            } catch (Exception e) {
                e.printStackTrace();
                // Mostrar mensaje de error en el hilo principal
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(context, "Error al obtener actividades", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
