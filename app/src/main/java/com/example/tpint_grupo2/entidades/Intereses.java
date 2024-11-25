package com.example.tpint_grupo2.entidades;

import com.example.tpint_grupo2.enums.EnumDias;

import java.util.ArrayList;

public class Intereses {
    //Nombre de las columnas en la BD
    public static final String NOMBRE_TABLA="intereses";
    public static final String column_id="idIntereses";
    public static final String column_usuario="nro_documento";
    public static final String column_dias_disponibles="dias_disponibles";
    public static final String column_horarios_disponibles="horarios_disponibles";
    public static final String column_transporte_propio="transporte_propio"; //ES DE TIPO TINYINT

    //COLUMNAS DE TABLA ZONAS PREFERIDAS
    public static final String zona_NOMBRE_TABLA="zonaspreferidas";
    public static final String zona_column_id="idZonasPreferidas";
    public static final String zona_column_idIntereses="idIntereses";
    public static final String zona_column_localidad="idLocalidad";

    //COLUMNAS DE TABLA ACTIVIDADES PREFERIDAS
    public static final String actividad_NOMBRE_TABLA="actividadespreferidas";
    public static final String actividad_column_id="idActividadesPreferidas";
    public static final String actividad_column_idIntereses="idInteres";
    public static final String actividad_column_idCategoria="idCategoria";


    //ATRIBUTOS
    private int id;
    private Usuario usuario;
    private ArrayList<EnumDias> diasDisponibles;
    private String horario;
    private boolean transporte;
    private ArrayList<Localidad> localidades; /// CORRESPONDE A LA TABLA ZONAS PREFERIDAS
    private ArrayList<Categoria> categorias; /// CORRESPONDE A LA TABLA ACTIVIDADES PREFERIDAS

    public Intereses() {
    }

    public Intereses(int id, Usuario usuario, ArrayList<EnumDias> diasDisponibles, String horario, boolean transporte, ArrayList<Localidad> localidades, ArrayList<Categoria> categorias) {
        this.id = id;
        this.usuario = usuario;
        this.diasDisponibles = diasDisponibles;
        this.horario = horario;
        this.transporte = transporte;
        this.localidades = localidades;
        this.categorias = categorias;
    }


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

    public ArrayList<EnumDias> getDiasDisponibles() {
        return diasDisponibles;
    }

    public void setDiasDisponibles(ArrayList<EnumDias> diasDisponibles) {
        this.diasDisponibles = diasDisponibles;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public boolean isTransporte() {
        return transporte;
    }

    public void setTransporte(boolean transporte) {
        this.transporte = transporte;
    }

    public ArrayList<Localidad> getLocalidades() {
        return localidades;
    }

    public void setLocalidades(ArrayList<Localidad> localidades) {
        this.localidades = localidades;
    }

    public ArrayList<Categoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(ArrayList<Categoria> categorias) {
        this.categorias = categorias;
    }
}
