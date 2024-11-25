package com.example.tpint_grupo2.entidades;

import com.example.tpint_grupo2.enums.EnumNacionalidades;
import com.example.tpint_grupo2.enums.EnumTiposUsuario;

import java.io.Serializable;
import java.time.LocalDate;

public class Usuario  implements Serializable {
    //Nombre de las columnas en la BD
    public static final String NOMBRE_TABLA="usuarios";
    public static final String column_id="nro_documento";
    public static final String column_tipo_usuario="tipo_usuario";
    public static final String column_contrasena="password";
    public static final String column_nombre="nombre";
    public static final String column_apellido="apellido";
    public static final String column_nacionalidad="nacionalidad";
    public static final String column_sexo="sexo";
    public static final String column_nacimiento="fecha_nacimiento";
    public static final String column_localidad="id_localidad";
    public static final String column_domicilio="domicilio";
    public static final String column_telefono="nro_telefono";
    public static final String column_email="email";
    public static final String column_institucion="institucion";

    //Atributos
    private int nro_documento;
    private EnumTiposUsuario tipo_usuario;
    private String password;
    private String nombre;
    private String apellido;
    private String domicilio;
    private EnumNacionalidades nacionalidad;
    private String sexo;
    private LocalDate nacimiento;
    private String telefono;
    private String email;
    private String institucion;
    private Localidad localidad;


    public Usuario() {
    }


    public int getNro_documento() {
        return nro_documento;
    }

    public void setNro_documento(int nro_documento) {
        this.nro_documento = nro_documento;
    }

    public EnumTiposUsuario getTipo_usuario() {
        return tipo_usuario;
    }

    public void setTipo_usuario(EnumTiposUsuario tipo_usuario) {
        this.tipo_usuario = tipo_usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public EnumNacionalidades getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(EnumNacionalidades nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public LocalDate getNacimiento() {
        return nacimiento;
    }

    public void setNacimiento(LocalDate nacimiento) {
        this.nacimiento = nacimiento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInstitucion() {
        return institucion;
    }

    public void setInstitucion(String institucion) {
        this.institucion = institucion;
    }

    public Localidad getLocalidad() {
        return localidad;
    }

    public void setLocalidad(Localidad localidad) {
        this.localidad = localidad;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }
}
