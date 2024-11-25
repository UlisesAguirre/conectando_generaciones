package com.example.tpint_grupo2.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataDB {

    public static String host="utn-conectando-generaciones.mysql.database.azure.com";
    public static String port="3306";
    public static String nameBD="db_conectando_generaciones";
    public static String user="admin_123";
    public static String pass="Contrasena789";

    //Conexion
    public static String urlMySQL = "jdbc:mysql://utn-conectando-generaciones.mysql.database.azure.com:3306/db_conectando_generaciones?useSSL=false&serverTimezone=UTC";

    public static String driver = "com.mysql.jdbc.Driver";
}

