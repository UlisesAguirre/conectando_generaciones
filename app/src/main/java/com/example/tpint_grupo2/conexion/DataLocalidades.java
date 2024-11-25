package com.example.tpint_grupo2.conexion;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.example.tpint_grupo2.entidades.Localidad;
import com.example.tpint_grupo2.entidades.Provincia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataLocalidades {
    private Context context;
    private ExecutorService executorService;
    private DataProvincias dataProvincias;
    public DataLocalidades(Context context) {
        this.context = context;
        this.executorService = Executors.newSingleThreadExecutor();
        this.dataProvincias = new DataProvincias(context);  // Pasamos el context
    }
    public void obtenerLocalidadPorId(int idLocalidad, OnLocalidadRetrievedCallback callback) {
        executorService.execute(() -> {
            try {
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                String query = "SELECT * FROM " + Localidad.NOMBRE_TABLA + " WHERE " + Localidad.column_id + " = " + idLocalidad;
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                if (rs.next()) {
                    int idProvincia = rs.getInt(Localidad.column_provincia_id);
                    String nombreLocalidad = rs.getString(Localidad.column_nombre);

                    dataProvincias.obtenerProvinciaPorId(idProvincia, provincia -> {
                        Localidad localidad = new Localidad(idLocalidad, provincia, nombreLocalidad);
                        new Handler(Looper.getMainLooper()).post(() -> callback.onLocalidadRetrieved(localidad));
                    });
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onLocalidadRetrieved(null));
                }

                rs.close();
                st.close();
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> callback.onLocalidadRetrieved(null));
            }
        });
    }

    public interface OnLocalidadRetrievedCallback {
        void onLocalidadRetrieved(Localidad localidad);
    }

    public void obtenerTodasLasLocalidades(OnLocalidadesRetrievedCallback callback) {
        executorService.execute(() -> {
            List<Localidad> localidades = new ArrayList<>();
            try (Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                 Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT * FROM " + Localidad.NOMBRE_TABLA)) {

                while (rs.next()) {
                    int id = rs.getInt(Localidad.column_id);
                    int idProvincia = rs.getInt(Localidad.column_provincia_id);
                    String nombre = rs.getString(Localidad.column_nombre);
                    Provincia provincia = new Provincia(idProvincia, "");
                    localidades.add(new Localidad(id, provincia, nombre));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            new Handler(Looper.getMainLooper()).post(() -> callback.onLocalidadesRetrieved(localidades));
        });
    }


    public interface OnLocalidadesRetrievedCallback {
        void onLocalidadesRetrieved(List<Localidad> localidades);
    }
}