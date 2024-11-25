package com.example.tpint_grupo2.entidades;

public class Categoria {
    //NOMBRES DE COLUMNAS BD
    public static final String NOMBRE_TABLA="categorias";
    public static final String column_Id="idCategoria";
    public static final String column_titulo="titulo";
    public static final String column_descripcion="descripcion";

    private int idCategoria;
    private String titulo;
    private String descripcion;


    public Categoria(){

    };

    public Categoria(int idCategoria, String titulo, String descripcion) {
        this.idCategoria = idCategoria;
        this.titulo = titulo;
        this.descripcion = descripcion;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "Categoria{" +
                "idCategoria=" + idCategoria +
                ", titulo='" + titulo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
