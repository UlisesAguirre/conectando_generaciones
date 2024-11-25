package com.example.tpint_grupo2.queries;

import android.os.Message;

import androidx.fragment.app.Fragment;

import com.example.tpint_grupo2.conexion.DataDB;
import com.example.tpint_grupo2.entidades.ActividadAgenda;
import com.example.tpint_grupo2.entidades.Categoria;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Handler;

public class ActividadesCoordinadas {

    public static List<ActividadAgenda> GetAllActividadesCoordinadas(String query) {
        ArrayList<ActividadAgenda> listaActividadesCoordinadas = new ArrayList<>();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {



            try {

                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL,DataDB.user,DataDB.pass);

                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                while (rs.next()) {
                    ActividadAgenda actividad = new ActividadAgenda(
                            rs.getInt("idActividadCoordinada"),
                            rs.getString("titulo"),
                            rs.getString("fecha_a_participar"),
                            rs.getString("nombre")+ " " + rs.getString("apellido"),
                            rs.getString("estado"),
                            rs.getString("calificacion"),
                            rs.getString("opinion")
                    );

                    listaActividadesCoordinadas.add(actividad);
                    System.out.println(actividad.getNombre());
                }


                rs.close();
                st.close();
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }


        });

        return listaActividadesCoordinadas;
    }
}
