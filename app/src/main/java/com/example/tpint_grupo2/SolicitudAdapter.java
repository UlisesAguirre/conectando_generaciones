package com.example.tpint_grupo2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import com.example.tpint_grupo2.conexion.DataMisSolicitudes;
import com.example.tpint_grupo2.conexion.DataNotificaciones;
import com.example.tpint_grupo2.entidades.ActividadCoordinada;
import com.example.tpint_grupo2.entidades.Notificacion;
import com.example.tpint_grupo2.entidades.Solicitud;
import com.example.tpint_grupo2.entidades.Usuario;
import com.example.tpint_grupo2.enums.EnumEstadosActividades;
import com.example.tpint_grupo2.enums.EnumTipoNotificaciones;
import com.example.tpint_grupo2.enums.EnumTiposUsuario;
import com.example.tpint_grupo2.sessions.UsuariosSession;

public class SolicitudAdapter extends RecyclerView.Adapter<SolicitudAdapter.SolicitudViewHolder> {

    private final List<ActividadCoordinada> solicitudes;
    private Notificacion notiNueva;
    private UsuariosSession usuariosSession;


    public SolicitudAdapter(List<ActividadCoordinada> solicitudes) {
        this.solicitudes = solicitudes;
    }

    @NonNull
    @Override
    public SolicitudViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_solicitud, parent, false);

        usuariosSession=new UsuariosSession(parent.getContext());

        return new SolicitudViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SolicitudViewHolder holder, int position) {
        ActividadCoordinada solicitud = solicitudes.get(position);
        holder.tvNombre.setText("Nombre actividad: " + solicitud.getActividad().getTitulo());
        holder.tvFecha.setText("Fecha: " + solicitud.getFechaParticipar());
        holder.tvOrganizador.setText("Organizador: " + solicitud.getVoluntario().getNombre() + " " + solicitud.getVoluntario().getApellido());

        DataMisSolicitudes service = new DataMisSolicitudes();


        notiNueva=new Notificacion();
        notiNueva.setActividadCoordinada(solicitud);
        notiNueva.setUsuarioDestino(solicitud.getVoluntario());
        notiNueva.setUsuario(usuariosSession.getUser());


        holder.btnRechazar.setOnClickListener(v -> {
            notiNueva.setTipoNotificacion(EnumTipoNotificaciones.Rechazo);
            notiNueva.setEstado("RECIBIDA");
            mostrarDialogoConfirmacion(v, "¿Estás seguro de rechazar esta solicitud?", EnumEstadosActividades.RECHAZADA, solicitud.getIdActividadCoordinada(), position, solicitud.getVoluntario().getTipo_usuario(),notiNueva);
        });

        holder.btnAceptar.setOnClickListener(v -> {
            notiNueva.setTipoNotificacion(EnumTipoNotificaciones.Confirmacion);
            notiNueva.setEstado("RECIBIDA");
            mostrarDialogoConfirmacion(v, "¿Estás seguro de aceptar esta solicitud?", EnumEstadosActividades.ACEPTADA, solicitud.getIdActividadCoordinada(),position, solicitud.getVoluntario().getTipo_usuario(),notiNueva);
        });
    }

    private void mostrarDialogoConfirmacion(View view, String mensaje, EnumEstadosActividades estado, int idActividad, int position, EnumTiposUsuario tipoUsuario, Notificacion notiNueva) {
        new AlertDialog.Builder(view.getContext())
                .setMessage(mensaje)
                .setCancelable(false)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        DataMisSolicitudes service = new DataMisSolicitudes();
                        service.UpdateEstadoSolicitud(estado, idActividad, tipoUsuario);

                        DataNotificaciones dataNotificaciones=new DataNotificaciones();
                        dataNotificaciones.insertarNotificacion(notiNueva);

                        solicitudes.remove(position);

                        notifyItemRemoved(position);

                       String mensajeConfirmacion = estado == EnumEstadosActividades.ACEPTADA ?
                                "Solicitud aceptada" : "Solicitud rechazada";

                        new AlertDialog.Builder(view.getContext())
                                .setMessage(mensajeConfirmacion)
                                .setCancelable(false)
                                .setPositiveButton("Aceptar", null)
                                .show();

                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return solicitudes.size();
    }

    public static class SolicitudViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvFecha, tvOrganizador;
        Button btnRechazar, btnAceptar;

        public SolicitudViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvOrganizador = itemView.findViewById(R.id.tvOrganizador);
            btnRechazar = itemView.findViewById(R.id.btnRechazar);
            btnAceptar = itemView.findViewById(R.id.btnAceptar);
        }
    }
}
