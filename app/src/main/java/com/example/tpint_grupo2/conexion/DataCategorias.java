package com.example.tpint_grupo2.conexion;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.tpint_grupo2.entidades.Categoria;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class DataCategorias {

    private Context context;
    private ExecutorService executorService;

    public DataCategorias(Context context) {
        this.context = context;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void obtenerCategorias(Consumer<List<Categoria>> callback) {
        executorService.execute(() -> {
            List<Categoria> categorias = new ArrayList<>();
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                String query = "SELECT * FROM categorias";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                while (rs.next()) {
                    Categoria categoria = new Categoria();
                    categoria.setIdCategoria(rs.getInt(Categoria.column_Id));
                    categoria.setTitulo(rs.getString(Categoria.column_titulo));
                    categoria.setDescripcion(rs.getString(Categoria.column_descripcion));
                    categorias.add(categoria);
                }

                rs.close();
                st.close();
                con.close();

                new Handler(Looper.getMainLooper()).post(() -> callback.accept(categorias));

            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(context, "Error al obtener categorías", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
