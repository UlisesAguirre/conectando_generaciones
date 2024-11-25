package com.example.tpint_grupo2.entidades;

import com.example.tpint_grupo2.enums.EnumEstadosActividades;

public class ActividadCoordinada {
    // Nombre de las columnas en la BD
    public static final String NOMBRE_TABLA = "actividadescoordinadas";
    public static final String column_id = "idactividadCoordinada";
    public static final String column_id_actividad = "idActividad";
    public static final String column_dni_voluntario = "dni_usuario_voluntario";
    public static final String column_dni_adulto_mayor = "dni_usuario_adulto_mayor";
    public static final String column_fecha_a_participar = "fecha_a_participar";
    public static final String column_fecha_solicitud = "fecha_solicitud";
    public static final String column_horario_inicio = "horario_inicio";
    public static final String column_estado_voluntario = "estado_voluntario";
    public static final String column_estado_adulto = "estado_adulto";

    // Atributos
    private int idActividadCoordinada;
    private Actividad actividad;
    private Usuario voluntario;
    private Usuario adultoMayor;
    private String fechaParticipar;
    private String fechaSolicitud;
    private String horario;
    private EnumEstadosActividades estadoActividad;

    // Atributos adicionales para manejar directamente las columnas de la base de datos
    private int idActividadBD; // idActividad (INT)
    private int dniVoluntarioBD; // dni_usuario_voluntario (INT)
    private int dniAdultoMayorBD; // dni_usuario_adulto_mayor (INT)
    private String fechaParticiparBD; // fecha_a_participar (String)
    private String fechaSolicitudBD; // fecha_solicitud (String)
    private String horarioInicioBD; // horario_inicio (String)
    private String estadoVoluntarioBD; // estado_voluntario (String)
    private String estadoAdultoBD; // estado_adulto (String)

    public ActividadCoordinada() {
    }

    public ActividadCoordinada(int idActividadCoordinada, Actividad actividad, Usuario voluntario, Usuario adultoMayor,
                               String fechaParticipar, String fechaSolicitud, String horario, EnumEstadosActividades estadoActividad,
                               int idActividadBD, int dniVoluntarioBD, int dniAdultoMayorBD, String fechaParticiparBD,
                               String fechaSolicitudBD, String horarioInicioBD, String estadoVoluntarioBD, String estadoAdultoBD) {
        this.idActividadCoordinada = idActividadCoordinada;
        this.actividad = actividad;
        this.voluntario = voluntario;
        this.adultoMayor = adultoMayor;
        this.fechaParticipar = fechaParticipar;
        this.fechaSolicitud = fechaSolicitud;
        this.horario = horario;
        this.estadoActividad = estadoActividad;

        // Nuevos atributos para manejar datos específicos de la base de datos
        this.idActividadBD = idActividadBD;
        this.dniVoluntarioBD = dniVoluntarioBD;
        this.dniAdultoMayorBD = dniAdultoMayorBD;
        this.fechaParticiparBD = fechaParticiparBD;
        this.fechaSolicitudBD = fechaSolicitudBD;
        this.horarioInicioBD = horarioInicioBD;
        this.estadoVoluntarioBD = estadoVoluntarioBD;
        this.estadoAdultoBD = estadoAdultoBD;
    }

    // Getters y setters de los atributos
    public int getIdActividadCoordinada() {
        return idActividadCoordinada;
    }

    public void setIdActividadCoordinada(int idActividadCoordinada) {
        this.idActividadCoordinada = idActividadCoordinada;
    }

    public Actividad getActividad() {
        return actividad;
    }

    public void setActividad(Actividad actividad) {
        this.actividad = actividad;
    }

    public Usuario getVoluntario() {
        return voluntario;
    }

    public void setVoluntario(Usuario voluntario) {
        this.voluntario = voluntario;
    }

    public Usuario getAdultoMayor() {
        return adultoMayor;
    }

    public void setAdultoMayor(Usuario adultoMayor) {
        this.adultoMayor = adultoMayor;
    }

    public String getFechaParticipar() {
        return fechaParticipar;
    }

    public void setFechaParticipar(String fechaParticipar) {
        this.fechaParticipar = fechaParticipar;
    }

    public String getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(String fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public EnumEstadosActividades getEstadoActividad() {
        return estadoActividad;
    }

    public void setEstadoActividad(EnumEstadosActividades estadoActividad) {
        this.estadoActividad = estadoActividad;
    }

    // Getters y setters para los nuevos atributos (BD)
    public int getIdActividadBD() {
        return idActividadBD;
    }

    public void setIdActividadBD(int idActividadBD) {
        this.idActividadBD = idActividadBD;
    }

    public int getDniVoluntarioBD() {
        return dniVoluntarioBD;
    }

    public void setDniVoluntarioBD(int dniVoluntarioBD) {
        this.dniVoluntarioBD = dniVoluntarioBD;
    }

    public int getDniAdultoMayorBD() {
        return dniAdultoMayorBD;
    }

    public void setDniAdultoMayorBD(int dniAdultoMayorBD) {
        this.dniAdultoMayorBD = dniAdultoMayorBD;
    }

    public String getFechaParticiparBD() {
        return fechaParticiparBD;
    }

    public void setFechaParticiparBD(String fechaParticiparBD) {
        this.fechaParticiparBD = fechaParticiparBD;
    }

    public String getFechaSolicitudBD() {
        return fechaSolicitudBD;
    }

    public void setFechaSolicitudBD(String fechaSolicitudBD) {
        this.fechaSolicitudBD = fechaSolicitudBD;
    }

    public String getHorarioInicioBD() {
        return horarioInicioBD;
    }

    public void setHorarioInicioBD(String horarioInicioBD) {
        this.horarioInicioBD = horarioInicioBD;
    }

    public String getEstadoVoluntarioBD() {
        return estadoVoluntarioBD;
    }

    public void setEstadoVoluntarioBD(String estadoVoluntarioBD) {
        this.estadoVoluntarioBD = estadoVoluntarioBD;
    }

    public String getEstadoAdultoBD() {
        return estadoAdultoBD;
    }

    public void setEstadoAdultoBD(String estadoAdultoBD) {
        this.estadoAdultoBD = estadoAdultoBD;
    }
}
