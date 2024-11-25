package com.example.tpint_grupo2.entidades;

import com.example.tpint_grupo2.enums.EnumDias;

import java.util.ArrayList;

public class Actividad {
    //Nombre de las columnas en la BD
    public static final String NOMBRE_TABLA="actividades";
    public static final String column_id="idActividad";
    public static final String column_categoria_id="idCategoria";
    public static final String column_localidad_id="idLocalidad";
    public static final String column_titulo="titulo";
    public static final String column_dias_dictados="dias_dictados";
    public static final String column_horario="horario";
    public static final String column_estado="estado";

    private int idActividad;
    private Categoria categoria;
    private Localidad localidad;
    private String titulo;
    private ArrayList<EnumDias> enumDias_dictados;
    private String horario;
    private Boolean estado;


    public Actividad() {

    }

    public Actividad(int idActividad, Categoria categoria, Localidad localidad, String titulo, ArrayList<EnumDias> enumDias_dictados, String horario) {
        this.idActividad = idActividad;
        this.categoria = categoria;
        this.localidad = localidad;
        this.titulo = titulo;
        this.enumDias_dictados = enumDias_dictados;
        this.horario = horario;
    }


    public int getIdActividad() {
        return idActividad;
    }

    public void setIdActividad(int idActividad) {
        this.idActividad = idActividad;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Localidad getLocalidad() {
        return localidad;
    }

    public void setLocalidad(Localidad localidad) {
        this.localidad = localidad;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public ArrayList<EnumDias> getDias_dictados() {
        return enumDias_dictados;
    }

    public String getDiasDictadosComoString() {
        if (enumDias_dictados == null || enumDias_dictados.isEmpty()) {
            return "Sin días"; // Valor predeterminado si no hay días disponibles
        }
        StringBuilder dias = new StringBuilder();
        for (EnumDias dia : enumDias_dictados) {
            if (dias.length() > 0) {
                dias.append("-"); // Separador entre días
            }
            dias.append(dia.name()); // Usa directamente el nombre del enum
        }
        return dias.toString();
    }


    public void setDias_dictados(ArrayList<EnumDias> enumDias_dictados) {
        this.enumDias_dictados = enumDias_dictados;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }



    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }
}
