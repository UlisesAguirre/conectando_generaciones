package com.example.tpint_grupo2.entidades;

public class Solicitud {

    private final int id;
    private final String nombre;
    private final String fecha;
    private final String organizador;

    public Solicitud(int id, String nombre, String fecha, String organizador) {

        this.id = id;
        this.nombre = nombre;
        this.fecha = fecha;
        this.organizador = organizador;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getFecha() { return fecha; }
    public String getOrganizador() { return organizador; }
}
