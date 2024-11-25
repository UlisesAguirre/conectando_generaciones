package com.example.tpint_grupo2.entidades;

public class ActividadAgenda {

    private int id;
    private String nombre;
    private String fecha;
    private String organizador;
    private String estado;
    private String clasificacion;
    private String comentario;

    public ActividadAgenda(int id, String nombre, String fecha,String organizador, String estado, String clasificacion,
                            String comentario) {
        this.id = id;
        this.nombre = nombre;
        this.fecha = fecha;
        this.organizador = organizador;
        this.estado = estado;
        this.clasificacion = clasificacion;
        this.comentario = comentario;
    }

    public ActividadAgenda() {
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getFecha() { return fecha; }
    public String getOrganizador() { return organizador; }
    public String getEstado() { return estado; }
    public String getClasificacion() { return clasificacion; }
    public String getComentario() { return comentario; }

    @Override
    public String toString() {
        return "ActividadAgenda{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", fecha='" + fecha + '\'' +
                ", organizador='" + organizador + '\'' +
                ", estado='" + estado + '\'' +
                ", clasificacion='" + clasificacion + '\'' +
                ", comentario='" + comentario + '\'' +
                '}';
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setOrganizador(String organizador) {
        this.organizador = organizador;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setClasificacion(String clasificacion) {
        this.clasificacion = clasificacion;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
