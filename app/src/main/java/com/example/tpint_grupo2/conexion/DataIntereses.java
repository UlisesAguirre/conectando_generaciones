package com.example.tpint_grupo2.conexion;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.tpint_grupo2.entidades.Categoria;
import com.example.tpint_grupo2.entidades.Intereses;
import com.example.tpint_grupo2.entidades.Localidad;
import com.example.tpint_grupo2.entidades.Usuario;
import com.example.tpint_grupo2.enums.EnumDias;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataIntereses {

    private Context context;
    private ExecutorService executorService;
    private DataLocalidades dataLocalidades;

    public DataIntereses(Context context) {
        this.context = context;
        this.executorService = Executors.newSingleThreadExecutor();
        this.dataLocalidades = new DataLocalidades(context);
    }

    public void registrarIntereses(Intereses intereses, RegistroInteresesCallback callback) {
        executorService.execute(() -> {
            boolean exito = false;
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);

                // Insertar en la tabla de intereses
                String query = "INSERT INTO " + Intereses.NOMBRE_TABLA +
                        "(nro_documento, dias_disponibles, horarios_disponibles, transporte_propio) " +
                        "VALUES (?, ?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, intereses.getUsuario().getNro_documento());
                ps.setString(2, obtenerDiasDisponibles(intereses.getDiasDisponibles()));
                ps.setString(3, intereses.getHorario());
                ps.setBoolean(4, intereses.isTransporte());

                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                int idIntereses = 0;
                if (rs.next()) {
                    idIntereses = rs.getInt(1);
                }

                // Insertar en la tabla de zonas preferidas (localidades)
                for (Localidad localidad : intereses.getLocalidades()) {
                    query = "INSERT INTO " + Intereses.zona_NOMBRE_TABLA +
                            "(idIntereses, idLocalidad) VALUES (?, ?)";
                    PreparedStatement psZonas = con.prepareStatement(query);
                    psZonas.setInt(1, idIntereses);
                    psZonas.setInt(2, localidad.getIdLocalidad());
                    psZonas.executeUpdate();
                    psZonas.close();
                }

                // Insertar en la tabla de actividades preferidas (categorías)
                for (Categoria categoria : intereses.getCategorias()) {
                    query = "INSERT INTO " + Intereses.actividad_NOMBRE_TABLA +
                            "(idInteres, idCategoria) VALUES (?, ?)";
                    PreparedStatement psActividades = con.prepareStatement(query);
                    psActividades.setInt(1, idIntereses);
                    psActividades.setInt(2, categoria.getIdCategoria());
                    psActividades.executeUpdate();
                    psActividades.close();
                }

                ps.close();
                con.close();

                exito = true;

            } catch (Exception e) {
                Log.e("DataIntereses", "Error during Intereses saving: ", e);
            }

            boolean finalExito = exito;
            new Handler(Looper.getMainLooper()).post(() -> callback.onRegistroCompletado(finalExito));
        });
    }

    private String obtenerDiasDisponibles(ArrayList<EnumDias> diasDisponibles) {
        StringBuilder dias = new StringBuilder();
        for (EnumDias dia : diasDisponibles) {
            String diaFormatted = dia.name();
            dias.append(diaFormatted).append("-");
        }
        if (dias.length() > 0) {
            dias.setLength(dias.length() - 1);
        }
        return dias.toString();
    }


    public interface RegistroInteresesCallback {
        void onRegistroCompletado(boolean exito);
    }

    public void borrarIntereses(int nroDocumento, BorrarInteresesCallback callback) {
        executorService.execute(() -> {
            boolean exito = false;
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);

                String query = "SELECT idIntereses FROM " + Intereses.NOMBRE_TABLA + " WHERE nro_documento = ?";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setInt(1, nroDocumento);
                ResultSet rs = ps.executeQuery();
                int idIntereses = 0;
                if (rs.next()) {
                    idIntereses = rs.getInt("idIntereses");
                }

                if (idIntereses == 0) {
                    rs.close();
                    ps.close();
                    con.close();
                    boolean finalExito = false;
                    new Handler(Looper.getMainLooper()).post(() -> callback.onBorrarCompletado(finalExito));
                    return;
                }

                rs.close();
                ps.close();

                // Eliminar de la tabla actividades_preferidas
                query = "DELETE FROM " + Intereses.actividad_NOMBRE_TABLA + " WHERE idInteres = ?";
                PreparedStatement psActividades = con.prepareStatement(query);
                psActividades.setInt(1, idIntereses);
                psActividades.executeUpdate();
                psActividades.close();

                // Eliminar de la tabla zonas_preferidas
                query = "DELETE FROM " + Intereses.zona_NOMBRE_TABLA + " WHERE idIntereses = ?";
                PreparedStatement psZonas = con.prepareStatement(query);
                psZonas.setInt(1, idIntereses);
                psZonas.executeUpdate();
                psZonas.close();

                // Eliminar de la tabla intereses
                query = "DELETE FROM " + Intereses.NOMBRE_TABLA + " WHERE idIntereses = ?";
                PreparedStatement psIntereses = con.prepareStatement(query);
                psIntereses.setInt(1, idIntereses);
                psIntereses.executeUpdate();
                psIntereses.close();

                con.close();

                exito = true;

            } catch (Exception e) {
                e.printStackTrace();
            }

            boolean finalExito = exito;
            new Handler(Looper.getMainLooper()).post(() -> callback.onBorrarCompletado(finalExito));
        });
    }

    public interface BorrarInteresesCallback {
        void onBorrarCompletado(boolean exito);
    }

    public void obtenerInteresesPorDni(int nroDocumento, ObtenerInteresesCallback callback) {
        executorService.execute(() -> {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);

                String query = "SELECT * FROM " + Intereses.NOMBRE_TABLA + " WHERE nro_documento = ?";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setInt(1, nroDocumento);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    int idIntereses = rs.getInt(Intereses.column_id);
                    String diasDisponibles = rs.getString(Intereses.column_dias_disponibles);
                    String horario = rs.getString(Intereses.column_horarios_disponibles);
                    boolean transporte = rs.getBoolean(Intereses.column_transporte_propio);

                    Usuario usuario = new Usuario();
                    usuario.setNro_documento(nroDocumento);

                    ArrayList<EnumDias> listaDias = new ArrayList<>();
                    for (String dia : diasDisponibles.split("-")) {
                        listaDias.add(EnumDias.valueOf(dia));
                    }

                    query = "SELECT idLocalidad FROM " + Intereses.zona_NOMBRE_TABLA + " WHERE idIntereses = ?";
                    PreparedStatement psLocalidades = con.prepareStatement(query);
                    psLocalidades.setInt(1, idIntereses);
                    ResultSet rsLocalidades = psLocalidades.executeQuery();
                    ArrayList<Localidad> localidades = new ArrayList<>();
                    ArrayList<Integer> idsLocalidades = new ArrayList<>();  // Lista para almacenar los IDs de localidades

                    while (rsLocalidades.next()) {
                        idsLocalidades.add(rsLocalidades.getInt(Intereses.zona_column_localidad));
                    }

                    rsLocalidades.close();
                    psLocalidades.close();

                    for (int idLocalidad : idsLocalidades) {
                        dataLocalidades.obtenerLocalidadPorId(idLocalidad, localidad -> {
                            if (localidad != null) {
                                localidades.add(localidad);
                            }

                            if (localidades.size() == idsLocalidades.size()) {
                                finalizarIntereses(con, usuario, idIntereses, listaDias, horario, transporte, localidades, callback);
                            }
                        });
                    }

                } else {
                    // No se encontraron intereses
                    new Handler(Looper.getMainLooper()).post(() -> callback.onInteresesObtenidos(null));
                }

                rs.close();
                ps.close();
            } catch (Exception e) {
                Log.e("obtenerInteresesPorDni", "Error al obtener intereses: " + e.getMessage(), e);
                new Handler(Looper.getMainLooper()).post(() -> callback.onInteresesObtenidos(null));
            }
        });
    }

    private void finalizarIntereses(Connection con, Usuario usuario, int idIntereses, ArrayList<EnumDias> listaDias,
                                    String horario, boolean transporte, ArrayList<Localidad> localidades, ObtenerInteresesCallback callback) {
        executorService.execute(() -> {
            try {
                String query = "SELECT idCategoria FROM " + Intereses.actividad_NOMBRE_TABLA + " WHERE idInteres = ?";
                PreparedStatement psCategorias = con.prepareStatement(query);
                psCategorias.setInt(1, idIntereses);
                ResultSet rsCategorias = psCategorias.executeQuery();
                ArrayList<Categoria> categorias = new ArrayList<>();

                while (rsCategorias.next()) {
                    int idCategoria = rsCategorias.getInt(Intereses.actividad_column_idCategoria);
                    Categoria categoria = new Categoria(); // Se asume un método para obtener la categoría por ID si es necesario
                    categoria.setIdCategoria(idCategoria);
                    categorias.add(categoria);
                }

                rsCategorias.close();
                psCategorias.close();

                // Crear objeto Intereses
                Intereses intereses = new Intereses(
                        idIntereses,
                        usuario,
                        listaDias,
                        horario,
                        transporte,
                        localidades,
                        categorias
                );

                new Handler(Looper.getMainLooper()).post(() -> callback.onInteresesObtenidos(intereses));
                con.close();

            } catch (Exception e) {
                Log.e("finalizarIntereses", "Error al finalizar intereses: " + e.getMessage(), e);
                new Handler(Looper.getMainLooper()).post(() -> callback.onInteresesObtenidos(null));
            }
        });
    }

    public interface ObtenerInteresesCallback {
        void onInteresesObtenidos(Intereses intereses);
    }
}