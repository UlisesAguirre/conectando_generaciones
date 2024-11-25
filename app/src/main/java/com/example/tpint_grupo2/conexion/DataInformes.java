package com.example.tpint_grupo2.conexion;

import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.tpint_grupo2.entidades.Actividad;
import com.example.tpint_grupo2.entidades.ActividadCoordinada;
import com.example.tpint_grupo2.entidades.Categoria;
import com.example.tpint_grupo2.entidades.Notificacion;
import com.example.tpint_grupo2.entidades.PresenciaUsuarios;
import com.example.tpint_grupo2.entidades.Usuario;
import com.example.tpint_grupo2.enums.EnumEstadosActividades;
import com.example.tpint_grupo2.enums.EnumTipoNotificaciones;

import org.w3c.dom.Text;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataInformes {
    private TextView txt_cantidad_inscripciones;
    private TextView txt_valor_promedio;
    private TextView txt_porcentaje_asistencia;
    private TextView txt_solicitudes_enviadas;
    private TextView txt_actividad_filtrada;
    private TextView txt_detalles_informe;


    public DataInformes(){

    }

    public DataInformes(TextView txt_cantidad_inscripciones, TextView txt_valor_promedio,
                        TextView txt_porcentaje_asistencia, TextView txt_solicitudes_enviadas,
                        TextView txt_actividad_filtrada, TextView txt_detalles_informe) {
        this.txt_cantidad_inscripciones = txt_cantidad_inscripciones;
        this.txt_valor_promedio = txt_valor_promedio;
        this.txt_porcentaje_asistencia = txt_porcentaje_asistencia;
        this.txt_solicitudes_enviadas = txt_solicitudes_enviadas;
        this.txt_actividad_filtrada=txt_actividad_filtrada;
        this.txt_detalles_informe=txt_detalles_informe;
    }

    public TextView getTxt_cantidad_inscripciones() {
        return txt_cantidad_inscripciones;
    }
    public void setTxt_cantidad_inscripciones(TextView txt_cantidad_inscripciones) {
        this.txt_cantidad_inscripciones = txt_cantidad_inscripciones;
    }
    public TextView getTxt_valor_promedio() {
        return txt_valor_promedio;
    }
    public void setTxt_valor_promedio(TextView txt_valor_promedio) {
        this.txt_valor_promedio = txt_valor_promedio;
    }
    public TextView getTxt_porcentaje_asistencia() {
        return txt_porcentaje_asistencia;
    }
    public void setTxt_porcentaje_asistencia(TextView txt_porcentaje_asistencia) {
        this.txt_porcentaje_asistencia = txt_porcentaje_asistencia;
    }
    public TextView getTxt_solicitudes_enviadas() {
        return txt_solicitudes_enviadas;
    }
    public void setTxt_solicitudes_enviadas(TextView txt_solicitudes_enviadas) {
        this.txt_solicitudes_enviadas = txt_solicitudes_enviadas;
    }
    public TextView getTxt_actividad_filtrada() {
        return txt_actividad_filtrada;
    }
    public void setTxt_actividad_filtrada(TextView txt_actividad_filtrada) {
        this.txt_actividad_filtrada = txt_actividad_filtrada;
    }
    public TextView getTxt_detalles_informe() {
        return txt_detalles_informe;
    }
    public void setTxt_detalles_informe(TextView txt_detalles_informe) {
        this.txt_detalles_informe = txt_detalles_informe;
    }

    private int cantidadInscripciones=0;
    private float promedioCalificaciones=0;
    private int cantidadActividadesPasadas=0;
    private int cantidadActividadesPresentes=0;
    private int cantidadInvitacionesRecibidas=0;

    public void filtroPromedioCalificaciones(Categoria categoria, Date fechaInicio, Date fechaFin, Usuario usuarioLogueado, RatingBar ratingBar){
        String consulta="SELECT AVG("+PresenciaUsuarios.column_calificacion+") AS PROMEDIO " +
                "FROM "+ PresenciaUsuarios.NOMBRE_TABLA+
                " inner join "+ ActividadCoordinada.NOMBRE_TABLA+" on "+PresenciaUsuarios.NOMBRE_TABLA+"."+PresenciaUsuarios.column_id_actividadCoord+"="+ActividadCoordinada.NOMBRE_TABLA+"."+ActividadCoordinada.column_id +
                " inner join "+ Actividad.NOMBRE_TABLA+" on "+ Actividad.NOMBRE_TABLA+"."+Actividad.column_id+"="+ActividadCoordinada.NOMBRE_TABLA+"."+ActividadCoordinada.column_id_actividad+
                " inner join "+ Categoria.NOMBRE_TABLA+" on "+ Categoria.NOMBRE_TABLA+"."+Categoria.column_Id+"="+Actividad.NOMBRE_TABLA+"."+Actividad.column_categoria_id;

        boolean where=false; //Para verificar si falta esta cláusula
        if(usuarioLogueado!=null){
            consulta+=" WHERE "+ PresenciaUsuarios.column_documento+"="+usuarioLogueado.getNro_documento();
            where=true;
        }

        if(categoria!=null){
            if(where){
                consulta+=" AND ";
            }else{
                consulta+=" WHERE ";
                where=true;
            }
            consulta+=Categoria.NOMBRE_TABLA+"."+Categoria.column_Id+"="+categoria.getIdCategoria();
        }
        if(fechaInicio!=null){
            if(where){
                consulta+=" AND ";
            }else{
                consulta+=" WHERE ";
                where=true;
            }
            consulta+=ActividadCoordinada.NOMBRE_TABLA+"."+ActividadCoordinada.column_fecha_a_participar+">= '"+fechaInicio+"'";
        }
        if(fechaFin!=null){
            if(where){
                consulta+=" AND ";
            }else{
                consulta+=" WHERE ";
                where=true;
            }
            consulta+=ActividadCoordinada.NOMBRE_TABLA+"."+ActividadCoordinada.column_fecha_a_participar+"<= '"+fechaFin+"'";
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        String consultaFinal=consulta;
        executor.execute(() -> {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL,DataDB.user,DataDB.pass);
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(consultaFinal);
                while (rs.next()) {
                    promedioCalificaciones=rs.getFloat("PROMEDIO");
                }
                rs.close();
                st.close();
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            float promedioFinal;
            if(promedioCalificaciones!=0){
                //Redondeamos a dos decimales
                promedioFinal=Math.round(promedioCalificaciones* 100)/100 ;
            }else{
                promedioFinal=0;
            }
            //Vuelta al hilo principal
            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                //Cargo los valores a los TextView
                txt_valor_promedio.setText(String.valueOf(promedioFinal));
                if(ratingBar!=null){
                    ratingBar.setRating(promedioFinal);
                }
            });
        });
    };

    public void filtroCantidadInscripciones(Categoria categoria, Date fechaInicio, Date fechaFin, Usuario usuarioLogueado){
        String consulta="SELECT COUNT(*) AS CANTIDAD " +
                        "FROM "+ActividadCoordinada.NOMBRE_TABLA+
                        " inner join " + Actividad.NOMBRE_TABLA+ " on "+ Actividad.NOMBRE_TABLA+"."+Actividad.column_id+"="+ActividadCoordinada.NOMBRE_TABLA+"."+ActividadCoordinada.column_id_actividad;

        boolean where=false; //Para verificar si falta esta cláusula
        if(usuarioLogueado!=null){
            consulta+=" WHERE ("+ActividadCoordinada.column_dni_adulto_mayor+"="+usuarioLogueado.getNro_documento()+
                        " OR "+ActividadCoordinada.column_dni_voluntario+"="+usuarioLogueado.getNro_documento()+")";
            where=true;
        }
        if(categoria!=null){
            if(where){
                consulta+=" AND ";
            }else{
                consulta+=" WHERE ";
                where=true;
            }
            consulta+=Actividad.NOMBRE_TABLA+"."+Actividad.column_categoria_id+"="+categoria.getIdCategoria();
        }
        if(fechaInicio!=null){
            if(where){
                consulta+=" AND ";
            }else{
                consulta+=" WHERE ";
                where=true;
            }
            consulta+=ActividadCoordinada.NOMBRE_TABLA+"."+ActividadCoordinada.column_fecha_a_participar+">= '"+fechaInicio+"'";
        }
        if(fechaFin!=null){
            if(where){
                consulta+=" AND ";
            }else{
                consulta+=" WHERE ";
                where=true;
            }
            consulta+=ActividadCoordinada.NOMBRE_TABLA+"."+ActividadCoordinada.column_fecha_a_participar+"<= '"+fechaFin+"'";
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        String consultaFinal=consulta;
        executor.execute(() -> {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL,DataDB.user,DataDB.pass);
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(consultaFinal);
                while (rs.next()) {
                    cantidadInscripciones=rs.getInt("CANTIDAD");
                }
                rs.close();
                st.close();
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Vuelta al hilo principal
            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                //Cargo los valores a los TextView
                txt_cantidad_inscripciones.setText(String.valueOf(cantidadInscripciones));

                String detallesInforme;
                detallesInforme="Informe sobre actividades registradas";
                if(categoria!=null){
                    txt_actividad_filtrada.setText(categoria.getTitulo());
                    detallesInforme+=" categoria: "+categoria.getTitulo().toUpperCase();

                }else {
                    txt_actividad_filtrada.setText("Todas las CATEGORIAS");
                }
                if(fechaInicio!=null){
                    detallesInforme+=" desde "+fechaInicio;
                }
                if(fechaFin!=null){
                    detallesInforme+=" hasta "+fechaFin;
                }
                detallesInforme+=".";

                txt_detalles_informe.setText(detallesInforme);
            });
        });
    };

    public void filtroPresenciaActividades(Categoria categoria, Date fechaInicio, Date fechaFin, Usuario usuarioLogueado){
        //1. Obtener TOTAL de Actividades pasadas (Tanto los PRESENTES como AUSENTES)
        String consulta="SELECT COUNT(*) AS ACTIVIDADES_PASADAS "+
                        " FROM "+PresenciaUsuarios.NOMBRE_TABLA +
                        " inner join "+ ActividadCoordinada.NOMBRE_TABLA+" on "+PresenciaUsuarios.NOMBRE_TABLA+"."+PresenciaUsuarios.column_id_actividadCoord+"="+ActividadCoordinada.NOMBRE_TABLA+"."+ActividadCoordinada.column_id +
                        " inner join "+ Actividad.NOMBRE_TABLA+" on "+ Actividad.NOMBRE_TABLA+"."+Actividad.column_id+"="+ActividadCoordinada.NOMBRE_TABLA+"."+ActividadCoordinada.column_id_actividad;

        boolean where=false; //Para verificar si falta esta cláusula

        //2. Obtener TOTAL de Actividades en la que estuvo presente
        String consultaPresente="SELECT COUNT(*) AS ACTIVIDADES_PRESENTE "+
                " FROM "+PresenciaUsuarios.NOMBRE_TABLA +
                " inner join "+ ActividadCoordinada.NOMBRE_TABLA+" on "+PresenciaUsuarios.NOMBRE_TABLA+"."+PresenciaUsuarios.column_id_actividadCoord+"="+ActividadCoordinada.NOMBRE_TABLA+"."+ActividadCoordinada.column_id +
                " inner join "+ Actividad.NOMBRE_TABLA+" on "+ Actividad.NOMBRE_TABLA+"."+Actividad.column_id+"="+ActividadCoordinada.NOMBRE_TABLA+"."+ActividadCoordinada.column_id_actividad+
                " WHERE "+PresenciaUsuarios.column_presencia+"= 'PRESENTE'";

        if(usuarioLogueado!=null){
            consulta+=" WHERE "+PresenciaUsuarios.column_documento+"="+usuarioLogueado.getNro_documento();
            consultaPresente+=" AND "+PresenciaUsuarios.column_documento+"="+usuarioLogueado.getNro_documento();
            where=true;
        }
        if(categoria!=null){
            consultaPresente+=" AND "+Actividad.NOMBRE_TABLA+"."+Actividad.column_categoria_id+"="+categoria.getIdCategoria();
            if(where){
                consulta+=" AND ";
            }else{
                consulta+=" WHERE ";
                where=true;
            }
            consulta+=Actividad.NOMBRE_TABLA+"."+Actividad.column_categoria_id+"="+categoria.getIdCategoria();
        }
        if(fechaInicio!=null){
            consultaPresente+=" AND "+ActividadCoordinada.NOMBRE_TABLA+"."+ActividadCoordinada.column_fecha_a_participar+">= '"+fechaInicio+"'";
            if(where){
                consulta+=" AND ";
            }else{
                consulta+=" WHERE ";
                where=true;
            }
            consulta+=ActividadCoordinada.NOMBRE_TABLA+"."+ActividadCoordinada.column_fecha_a_participar+">= '"+fechaInicio+"'";
        }
        if(fechaFin!=null){
            consultaPresente+=" AND "+ActividadCoordinada.NOMBRE_TABLA+"."+ActividadCoordinada.column_fecha_a_participar+"<= '"+fechaFin+"'";
            if(where){
                consulta+=" AND ";
            }else{
                consulta+=" WHERE ";
                where=true;
            }
            consulta+=ActividadCoordinada.NOMBRE_TABLA+"."+ActividadCoordinada.column_fecha_a_participar+"<= '"+fechaFin+"'";
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        String consultaFinal=consulta;
        String consultaFinalPresente=consultaPresente;
        executor.execute(() -> {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL,DataDB.user,DataDB.pass);
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(consultaFinal);
                while (rs.next()) {
                    cantidadActividadesPasadas=rs.getInt("ACTIVIDADES_PASADAS");
                }
                rs = st.executeQuery(consultaFinalPresente);
                while (rs.next()) {
                    cantidadActividadesPresentes=rs.getInt("ACTIVIDADES_PRESENTE");
                }

                rs.close();
                st.close();
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Calculo porcentaje de Asistencia
            float porcentaje;
            if(cantidadActividadesPresentes!=0){
                //Redondeamos a dos decimales
                porcentaje=Math.round(porcentaje(cantidadActividadesPasadas,cantidadActividadesPresentes)* 100)/100 ;
            }else{
                porcentaje=0;
            }

            //Vuelta al hilo principal
            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                //Cargo los valores a los TextView
                txt_porcentaje_asistencia.setText(String.valueOf(porcentaje+"%"));
            });
        });

    }

    public void filtroSolicitudesEnviadas(Categoria categoria, Date fechaInicio, Date fechaFin, Usuario usuarioLogueado){
        //Consulta para obtener todas las inscripciones
        String consultaTodo="SELECT COUNT(*) AS CANTIDAD " +
                "FROM "+ActividadCoordinada.NOMBRE_TABLA+
                " inner join " + Actividad.NOMBRE_TABLA+ " on "+ Actividad.NOMBRE_TABLA+"."+Actividad.column_id+"="+ActividadCoordinada.NOMBRE_TABLA+"."+ActividadCoordinada.column_id_actividad;

        //Consulta para obtener la cantidad de Invitaciones que Recibió
        String consulta="SELECT count(*) AS SOLICITUDES_RECIBIDAS " +
                        "FROM "+ Notificacion.NOMBRE_TABLA+
                        " inner join "+ ActividadCoordinada.NOMBRE_TABLA+" on "+Notificacion.NOMBRE_TABLA+"."+Notificacion.column_idActividad+"="+ActividadCoordinada.NOMBRE_TABLA+"."+ActividadCoordinada.column_id +
                        " inner join "+ Actividad.NOMBRE_TABLA+" on "+ Actividad.NOMBRE_TABLA+"."+Actividad.column_id+"="+ActividadCoordinada.NOMBRE_TABLA+"."+ActividadCoordinada.column_id_actividad+
                        " WHERE "+Notificacion.column_tipo+"= '"+EnumTipoNotificaciones.Invitacion+"'";

        boolean whereTodo=false; //Para verificar si falta esta cláusula

        if(usuarioLogueado!=null){
            //Consulta Solicitudes Recibidas
            consulta+=" AND "+Notificacion.column_nro_documento+"="+usuarioLogueado.getNro_documento();
            //Consulta todas las inscripciones
            consultaTodo+=" WHERE ("+ActividadCoordinada.column_dni_adulto_mayor+"="+usuarioLogueado.getNro_documento()+
                    " OR "+ActividadCoordinada.column_dni_voluntario+"="+usuarioLogueado.getNro_documento()+")";
            whereTodo=true;
        }
        if(categoria!=null){
            //Consulta Solicitudes Recibidas
            consulta+=" AND "+Actividad.NOMBRE_TABLA+"."+Actividad.column_categoria_id+"="+categoria.getIdCategoria();

            //Consulta para todas las inscripciones
            if(whereTodo){
                consultaTodo+=" AND ";
            }else{
                consultaTodo+=" WHERE ";
                whereTodo=true;
            }
            consultaTodo+=Actividad.NOMBRE_TABLA+"."+Actividad.column_categoria_id+"="+categoria.getIdCategoria();
        }
        if(fechaInicio!=null){
            //Consulta Solicitudes Recibidas
            consulta+=" AND "+ActividadCoordinada.NOMBRE_TABLA+"."+ActividadCoordinada.column_fecha_solicitud+">= '"+fechaInicio+"'";

            //Consulta para Todas las inscripciones
            if(whereTodo){
                consultaTodo+=" AND ";
            }else{
                consultaTodo+=" WHERE ";
                whereTodo=true;
            }
            consultaTodo+=ActividadCoordinada.NOMBRE_TABLA+"."+ActividadCoordinada.column_fecha_a_participar+">= '"+fechaInicio+"'";

        }
        if(fechaFin!=null){
            //Consulta Solicitudes Recibidas
           consulta+=" AND "+ActividadCoordinada.NOMBRE_TABLA+"."+ActividadCoordinada.column_fecha_solicitud+"<= '"+fechaFin+"'";

            //Consulta para todas las inscripciones
            if(whereTodo){
                consultaTodo+=" AND ";
            }else{
                consultaTodo+=" WHERE ";
                whereTodo=true;
            }
            consultaTodo+=ActividadCoordinada.NOMBRE_TABLA+"."+ActividadCoordinada.column_fecha_a_participar+"<= '"+fechaFin+"'";

        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        String consultaFinal=consulta;
        String consultaTodoFinal=consultaTodo;
        executor.execute(() -> {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL,DataDB.user,DataDB.pass);
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(consultaFinal);
                while (rs.next()) {
                    cantidadInvitacionesRecibidas=rs.getInt("SOLICITUDES_RECIBIDAS");
                }
                rs = st.executeQuery(consultaTodoFinal);
                while (rs.next()) {
                    cantidadInscripciones=rs.getInt("CANTIDAD");
                }
                rs.close();
                st.close();
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            int cantidadSolicitudesEnviadas=cantidadInscripciones-cantidadInvitacionesRecibidas;

            //Vuelta al hilo principal
            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                //Cargo los valores a los TextView
                txt_solicitudes_enviadas.setText(String.valueOf(cantidadSolicitudesEnviadas));
            });
        });
    };



    public float porcentaje(int total,int cantidad){
        float resultado=0;
        resultado=(cantidad*100)/total;
        return resultado;
    }



}


