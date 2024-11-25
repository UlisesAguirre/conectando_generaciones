package com.example.tpint_grupo2.entidades;

import java.io.Serializable;

public class Provincia implements Serializable {
    //Nombre de las columnas en la BD
    public static final String NOMBRE_TABLA="provincias";
    public static final String column_id="idProvincias";
    public static final String column_nombre="nombre";

    private int idProvincia;
    private String nombre;

    @Override
    public String toString() {
        return nombre;
    }

    public Provincia() {
    }

    public Provincia(int idProvincia, String nombre) {
        this.idProvincia = idProvincia;
        this.nombre = nombre;
    }

    public int getIdProvincia() {
        return idProvincia;
    }

    public void setIdProvincia(int idProvincia) {
        this.idProvincia = idProvincia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
