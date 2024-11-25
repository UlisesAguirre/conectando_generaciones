package com.example.tpint_grupo2.entidades;

public class PresenciaUsuarios {
    //Nombre de las columnas en la BD
    public static final String NOMBRE_TABLA="presencia_usuarios";
    public static final String column_id="idPresencia_usuarios";
    public static final String column_id_actividadCoord="idActividadCoordinada";
    public static final String column_documento="nro_documento";
    public static final String column_presencia="presencia";
    public static final String column_calificacion="calificacion";
    public static final String column_opinion="opinion";

    //Atributos
    private int id;
    private ActividadCoordinada actividad;
    private Usuario usuario;
    private String presencia;
    private float calificacion;
    private String opinion;


    public PresenciaUsuarios() {
    }

    public PresenciaUsuarios(int id, ActividadCoordinada actividad, String opinion, String presencia, Usuario usuario, float calificacion) {
        this.id = id;
        this.actividad = actividad;
        this.opinion = opinion;
        this.presencia = presencia;
        this.usuario = usuario;
        this.calificacion = calificacion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ActividadCoordinada getActividad() {
        return actividad;
    }

    public void setActividad(ActividadCoordinada actividad) {
        this.actividad = actividad;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getPresencia() {
        return presencia;
    }

    public void setPresencia(String presencia) {
        this.presencia = presencia;
    }

    public float getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(float calificacion) {
        this.calificacion = calificacion;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }
}

