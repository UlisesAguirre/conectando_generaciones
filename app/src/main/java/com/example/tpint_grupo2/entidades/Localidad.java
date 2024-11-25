package com.example.tpint_grupo2.entidades;

import java.io.Serializable;

public class Localidad implements Serializable {
    //Nombre de las columnas en la BD
    public static final String NOMBRE_TABLA="localidades";
    public static final String column_id="idlocalidad";
    public static final String column_nombre="nombre";
    public static final String column_provincia_id ="idProvincia";


    private int idLocalidad;
    private Provincia provincia;
    private String nombre;


    public Localidad() {
    }



    public Localidad(int idLocalidad, Provincia provincia, String nombre) {
        this.idLocalidad = idLocalidad;
        this.provincia = provincia;
        this.nombre = nombre;
    }


    public int getIdLocalidad() {
        return idLocalidad;
    }

    public void setIdLocalidad(int idLocalidad) {
        this.idLocalidad = idLocalidad;
    }

    public Provincia getProvincia() {
        return provincia;
    }

    public void setProvincia(Provincia provincia) {
        this.provincia = provincia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    public String toString2() {
        return nombre;
    }
    @Override
    public String toString() {
        return "Localidad{" +
                "idLocalidad=" + idLocalidad +
                ", provincia=" + provincia +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}
