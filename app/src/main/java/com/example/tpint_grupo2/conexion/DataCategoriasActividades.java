package com.example.tpint_grupo2.conexion;

import android.os.AsyncTask;
import android.widget.Spinner;

import com.example.tpint_grupo2.adapter.AdapterCategoriaActividad;
import com.example.tpint_grupo2.entidades.Categoria;

import java.io.Console;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataCategoriasActividades {


    public void fetchData(Spinner spinnerCategoria) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            ArrayList<Categoria> listaCategorias = new ArrayList<>();
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL,DataDB.user,DataDB.pass);

                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM categorias");

                while (rs.next()) {
                    Categoria categoria = new Categoria();
                    categoria.setIdCategoria(rs.getInt(Categoria.column_Id));
                    categoria.setTitulo(rs.getString(Categoria.column_titulo));
                    categoria.setDescripcion(rs.getString(Categoria.column_descripcion));
                    listaCategorias.add(categoria);

                }
                rs.close();
                st.close();
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                AdapterCategoriaActividad adapter = new AdapterCategoriaActividad(spinnerCategoria.getContext(), listaCategorias);
                spinnerCategoria.setAdapter(adapter);
            });
        });
    }




}
