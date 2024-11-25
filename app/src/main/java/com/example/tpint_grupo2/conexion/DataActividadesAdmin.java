package com.example.tpint_grupo2.conexion;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.tpint_grupo2.conexion.interfacesAdmin.BuscarActCallback;
import com.example.tpint_grupo2.conexion.interfacesAdmin.DeleteLogicoActCallback;
import com.example.tpint_grupo2.conexion.interfacesAdmin.InsertActCallback;
import com.example.tpint_grupo2.conexion.interfacesAdmin.UpdateActCallback;
import com.example.tpint_grupo2.entidades.Actividad;
import com.example.tpint_grupo2.entidades.Categoria;
import com.example.tpint_grupo2.entidades.Localidad;
import com.example.tpint_grupo2.entidades.Provincia;
import com.example.tpint_grupo2.enums.EnumDias;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DataActividadesAdmin {

    public interface OnDataLoadedCallback {
        void onDataLoaded(ArrayList<?> data);
    }

    // Obtener categorías
    public void obtenerCategorias(OnDataLoadedCallback callback) {
        String query = "SELECT * FROM categorias";
        executeQuery(query, new OnDataLoadedCallback() {
            @Override
            public void onDataLoaded(ArrayList<?> dataList) {
                callback.onDataLoaded(dataList);
            }
        }, Categoria.class);
    }

    // Obtener localidades
    public void obtenerLocalidades(OnDataLoadedCallback callback) {
        String query = "SELECT * FROM localidades";
        executeQuery(query, new OnDataLoadedCallback() {
            @Override
            public void onDataLoaded(ArrayList<?> dataList) {
                callback.onDataLoaded(dataList);
            }
        }, Localidad.class);
    }

    private void executeQuery(String query, OnDataLoadedCallback callback, Class<?> entityClass) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            ArrayList<Object> results = new ArrayList<>();
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);

                PreparedStatement pst = con.prepareStatement(query);
                ResultSet rs = pst.executeQuery();

                if (entityClass.equals(Categoria.class)) {
                    while (rs.next()) {
                        int idCategoria = rs.getInt("idCategoria");
                        String titulo = rs.getString("titulo");
                        String descripcion = rs.getString("descripcion");

                        Categoria categoria = new Categoria(idCategoria, titulo, descripcion);
                        results.add(categoria);
                    }
                } else if (entityClass.equals(Localidad.class)) {
                    while (rs.next()) {
                        int idLocalidad = rs.getInt("idlocalidad");
                        String nombre = rs.getString("nombre");
                        int idProvincia = rs.getInt("idProvincia");

                        Provincia provincia = obtenerProvinciaPorId(idProvincia);

                        Localidad localidad = new Localidad(idLocalidad, provincia, nombre);
                        results.add(localidad);
                    }
                }

                rs.close();
                pst.close();
                con.close();
            } catch (Exception e) {
                Log.e("DataActividadesAdmin", "Error al obtener datos", e);
            }

            new Handler(Looper.getMainLooper()).post(() -> callback.onDataLoaded(results));
        });
    }

    private Provincia obtenerProvinciaPorId(int idProvincia) {
        Provincia provincia = null;
        String query = "SELECT * FROM provincias WHERE idProvincias = ?";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, idProvincia);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                String nombreProvincia = rs.getString("nombre");
                provincia = new Provincia(idProvincia, nombreProvincia);
            }
            rs.close();
            pst.close();
            con.close();
        } catch (Exception e) {
            Log.e("DataActividadesAdmin", "Error al obtener provincia", e);
        }
        return provincia;
    }

    public void insertarActividad(Integer idCategoria, Integer idLocalidad, String nombreActividad, String diasDictados, String horario, InsertActCallback callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);

                String query = "INSERT INTO actividades (idCategoria, idLocalidad, titulo, dias_dictados, horario) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pst = con.prepareStatement(query);

                pst.setInt(1, idCategoria);
                pst.setInt(2, idLocalidad);
                pst.setString(3, nombreActividad);
                pst.setString(4, diasDictados);
                pst.setString(5, horario);

                int rowsInserted = pst.executeUpdate();
                pst.close();
                con.close();

                if (rowsInserted > 0) {
                    new Handler(Looper.getMainLooper()).post(callback::onInsertSuccess);
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onInsertError("No se pudo insertar la actividad."));
                }
            } catch (Exception e) {
                Log.e("DataActividadesAdmin", "Error al insertar actividad", e);
                new Handler(Looper.getMainLooper()).post(() -> callback.onInsertError("Error al insertar actividad: " + e.getMessage()));
            }
        });
    }

    public void actualizarActividad(Integer idActividad, Integer idCategoria, Integer idLocalidad, String nombreActividad, String diasDictados, String horario, UpdateActCallback callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);

                String query = "UPDATE actividades SET idCategoria = ?, idLocalidad = ?, titulo = ?, dias_dictados = ?, horario = ? WHERE idActividad = ?";
                PreparedStatement pst = con.prepareStatement(query);

                pst.setInt(1, idCategoria);
                pst.setInt(2, idLocalidad);
                pst.setString(3, nombreActividad);
                pst.setString(4, diasDictados);
                pst.setString(5, horario);
                pst.setInt(6, idActividad);

                int rowsUpdated = pst.executeUpdate();
                pst.close();
                con.close();

                if (rowsUpdated > 0) {
                    new Handler(Looper.getMainLooper()).post(callback::onUpdateSuccess);
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onUpdateError("No se pudo actualizar la actividad."));
                }
            } catch (Exception e) {
                Log.e("DataActividadesAdmin", "Error al actualizar actividad", e);
                new Handler(Looper.getMainLooper()).post(() -> callback.onUpdateError("Error al actualizar actividad: " + e.getMessage()));
            }
        });
    }

    public void bajaLogicaActividad(int actividadId, DeleteLogicoActCallback callback){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);

                String query = "UPDATE actividades SET estado = 0 WHERE idActividad = ?";
                PreparedStatement pst = con.prepareStatement(query);
                pst.setInt(1, actividadId);

                int rowsUpdated = pst.executeUpdate();
                pst.close();
                con.close();

                if (rowsUpdated > 0) {
                    new Handler(Looper.getMainLooper()).post(callback::onDeleteSuccess);
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onDeleteError("No se pudo borrar la actividad."));
                }
            } catch (Exception e) {
                Log.e("DataActividadesAdmin", "Error al borrar actividad", e);
                new Handler(Looper.getMainLooper()).post(() -> callback.onDeleteError("Error al borrar actividad: " + e.getMessage()));
            }
        });
    }

    public void buscarActividades(Integer idCategoria, Integer idLocalidad, String nombreAct, String dia, BuscarActCallback callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<Actividad> actividades = new ArrayList<>();

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);

                String query = "SELECT * FROM actividades WHERE 1=1 AND estado=1";
                List<Object> parameters = new ArrayList<>();

                if (idCategoria != null) {
                    query += " AND idCategoria = ?";
                    parameters.add(idCategoria);
                }
                if (idLocalidad != null) {
                    query += " AND idLocalidad = ?";
                    parameters.add(idLocalidad);
                }
                if (nombreAct != null && !nombreAct.isEmpty()) {
                    query += " AND titulo LIKE ?";
                    parameters.add("%" + nombreAct + "%");
                }
                if (dia != null && !dia.isEmpty()) {
                    query += " AND dias_dictados LIKE ?";
                    parameters.add("%" + dia + "%");
                }

                PreparedStatement pst = con.prepareStatement(query);

                for (int i = 0; i < parameters.size(); i++) {
                    pst.setObject(i + 1, parameters.get(i));
                }

                ResultSet rs = pst.executeQuery();

                while (rs.next()) {
                    Actividad actividad = new Actividad();
                    actividad.setIdActividad(rs.getInt(Actividad.column_id));
                    actividad.setTitulo(rs.getString(Actividad.column_titulo));
                    actividad.setHorario(rs.getString(Actividad.column_horario));

                    int idCat = rs.getInt(Actividad.column_categoria_id);
                    int idLoc = rs.getInt(Actividad.column_localidad_id);
                    actividad.setCategoria(obtenerCategoriaPorId(idCat));
                    actividad.setLocalidad(obtenerLocalidadPorId(idLoc));

                    String diasStr = rs.getString(Actividad.column_dias_dictados);
                    ArrayList<EnumDias> enumDiasList = convertirDiasADiasEnum(diasStr);
                    actividad.setDias_dictados(enumDiasList);

                    actividades.add(actividad);
                }

                rs.close();
                pst.close();
                con.close();

                if (actividades.isEmpty()) {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onBuscarError("No se encontraron actividades activas."));
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onBuscarSuccess(actividades));
                }

            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onBuscarError("Error al buscar actividades: no se encontró ninguna."));
            }
        });
    }


    public Future<Actividad> buscarActividadPorID(int actividadId) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Future<Actividad> future = executorService.submit(() -> {
            Actividad actividad = null;
            try {
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);

                String query = "SELECT * FROM " + Actividad.NOMBRE_TABLA + " WHERE " + Actividad.column_id + " = ?";
                PreparedStatement pst = con.prepareStatement(query);
                pst.setInt(1, actividadId);

                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    actividad = new Actividad();
                    actividad.setIdActividad(rs.getInt(Actividad.column_id));
                    actividad.setTitulo(rs.getString(Actividad.column_titulo));
                    actividad.setHorario(rs.getString(Actividad.column_horario));
                    String diasDictados = rs.getString(Actividad.column_dias_dictados);
                    actividad.setDias_dictados(convertirDiasADiasEnum(diasDictados));
                    int idCategoria = rs.getInt(Actividad.column_categoria_id);
                    Categoria categoria = obtenerCategoriaPorId(idCategoria);
                    actividad.setCategoria(categoria);
                    int idLocalidad = rs.getInt(Actividad.column_localidad_id);
                    Localidad localidad = obtenerLocalidadPorId(idLocalidad);
                    actividad.setLocalidad(localidad);
                }

                rs.close();
                pst.close();
                con.close();
            } catch (Exception e) {
                Log.e("DataActividadesAdmin", "Error al obtener actividad", e);
            }
            return actividad;
        });

        return future;
    }


    private Categoria obtenerCategoriaPorId(int idCategoria) {
        Categoria categoria = null;

        try {
            Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
            String query = "SELECT * FROM categorias WHERE idCategoria = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, idCategoria);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                categoria = new Categoria();
                categoria.setIdCategoria(rs.getInt("idCategoria"));
                categoria.setTitulo(rs.getString("titulo"));
                categoria.setDescripcion(rs.getString("descripcion"));
            }

            rs.close();
            pst.close();
            con.close();
        } catch (Exception e) {
            Log.e("DataActividadesAdmin", "Error al obtener categoría", e);
        }

        return categoria;
    }

    private Localidad obtenerLocalidadPorId(int idLocalidad) {
        Localidad localidad = null;

        try {
            Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
            String query = "SELECT idlocalidad, nombre FROM localidades WHERE idLocalidad = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, idLocalidad);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                localidad = new Localidad();
                localidad.setIdLocalidad(rs.getInt("idlocalidad"));
                localidad.setNombre(rs.getString("nombre"));
            }

            rs.close();
            pst.close();
            con.close();
        } catch (Exception e) {
            Log.e("DataActividadesAdmin", "Error al obtener localidad", e);
        }

        return localidad;
    }

    private ArrayList<EnumDias> convertirDiasADiasEnum(String diasStr) {
        ArrayList<EnumDias> diasEnumList = new ArrayList<>();

        if (diasStr == null || diasStr.isEmpty()) {
            return diasEnumList;
        }

        String[] diasArray = diasStr.split("-\\s*");

        for (String dia : diasArray) {
            try {
                String diaNormalizado = dia.trim();
                Log.d("convertirDiasADiasEnum", "Día procesado: " + diaNormalizado);

                EnumDias diaEnum = EnumDias.valueOf(diaNormalizado);
                diasEnumList.add(diaEnum);

            } catch (IllegalArgumentException e) {
                Log.e("convertirDiasADiasEnum", "Día no reconocido: " + dia + " (" + dia.trim() + ")");
            }
        }
        return diasEnumList;
    }
}
