package com.example.tpint_grupo2.conexion;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.example.tpint_grupo2.entidades.Provincia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataProvincias {

    private Context context;
    private ExecutorService executorService;

    public DataProvincias(Context context) {
        this.context = context;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void obtenerProvinciaPorId(int idProvincia, OnProvinciaRetrievedCallback callback) {
        executorService.execute(() -> {
            Provincia provincia = null;
            try {
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                String query = "SELECT * FROM " + Provincia.NOMBRE_TABLA + " WHERE " + Provincia.column_id + " = " + idProvincia;
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                if (rs.next()) {
                    String nombreProvincia = rs.getString(Provincia.column_nombre);
                    provincia = new Provincia(idProvincia, nombreProvincia);
                }
                rs.close();
                st.close();
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Provincia finalProvincia = provincia;
            new Handler(Looper.getMainLooper()).post(() -> callback.onProvinciaRetrieved(finalProvincia));
        });
    }
    public interface OnProvinciaRetrievedCallback {
        void onProvinciaRetrieved(Provincia provincia);
    }

    public void obtenerProvincias(OnProvinciasRetrievedCallback callback) {
        executorService.execute(() -> {
            List<Provincia> provincias = new ArrayList<>();
            try (Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                 Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT * FROM " + Provincia.NOMBRE_TABLA)) {

                while (rs.next()) {
                    int id = rs.getInt(Provincia.column_id);
                    String nombre = rs.getString(Provincia.column_nombre);
                    provincias.add(new Provincia(id, nombre));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> callback.onProvinciasRetrieved(provincias));
        });
    }

    public interface OnProvinciasRetrievedCallback {
        void onProvinciasRetrieved(List<Provincia> provincias);
    }
}