package com.example.tpint_grupo2.entidades;

import com.example.tpint_grupo2.enums.EnumTipoNotificaciones;

import java.sql.Date;
import java.time.LocalDate;

public class Notificacion {
    // Nombre de las columnas en la BD
    public static final String NOMBRE_TABLA = "notificaciones";
    public static final String column_id = "idNotificaciones";
    public static final String column_nro_documento = "nro_documento";
    public static final String column_nro_documento_destino = "nro_documento_destino";
    public static final String column_tipo = "tipo_notificacion";
    public static final String column_fecha_envio = "fecha_envio";
    public static final String column_estado = "estado";
    public static final String column_idActividad = "idActividadCoordinada";

    // Atributos
    private int id;
    private Usuario usuario;
    private EnumTipoNotificaciones tipoNotificacion;
    private LocalDate fecha_envio;
    private String estado;
    private ActividadCoordinada actividadCoordinada;
    private String nombreRemitente; // Nuevo atributo para el nombre del remitente
    private Usuario usuarioDestino;

    // Constructores
    public Notificacion() {}

    public Notificacion(int id, Usuario usuario, LocalDate fecha_envio, EnumTipoNotificaciones tipoNotificacion, String estado, ActividadCoordinada actividad, String nombreRemitente) {
        this.id = id;
        this.usuario = usuario;
        this.fecha_envio = fecha_envio;
        this.tipoNotificacion = tipoNotificacion;
        this.estado = estado;
        this.actividadCoordinada = actividad;
        this.nombreRemitente = nombreRemitente;
    }

    // Métodos getter y setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public EnumTipoNotificaciones getTipoNotificacion() {
        return tipoNotificacion;
    }

    public void setTipoNotificacion(EnumTipoNotificaciones tipoNotificacion) {
        this.tipoNotificacion = tipoNotificacion;
    }

    public LocalDate getFecha_envio() {
        return fecha_envio;
    }

    public void setFecha_envio(LocalDate fecha_envio) {
        this.fecha_envio = fecha_envio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public ActividadCoordinada getActividadCoordinada() {
        return actividadCoordinada;
    }

    public void setActividadCoordinada(ActividadCoordinada actividadCoordinada) {
        this.actividadCoordinada = actividadCoordinada;
    }

    public String getNombreRemitente() {
        return nombreRemitente;
    }

    public void setNombreRemitente(String nombreRemitente) {
        this.nombreRemitente = nombreRemitente;
    }
    public Usuario getUsuarioDestino() {
        return usuarioDestino;
    }

    public void setUsuarioDestino(Usuario usuarioDestino) {
        this.usuarioDestino = usuarioDestino;
    }
}
