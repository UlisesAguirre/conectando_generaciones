package com.example.tpint_grupo2.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tpint_grupo2.R;
import com.example.tpint_grupo2.conexion.DataNotificaciones;
import com.example.tpint_grupo2.entidades.Notificacion;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NotificacionesAdapter extends RecyclerView.Adapter<NotificacionesAdapter.NotificacionViewHolder> {

    private List<Notificacion> notificaciones;

    public NotificacionesAdapter() {
        this.notificaciones = new ArrayList<>();
    }

    @NonNull
    @Override
    public NotificacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notificacion, parent, false);
        return new NotificacionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificacionViewHolder holder, int position) {
        Notificacion notificacion = notificaciones.get(position);
        holder.bind(notificacion);

        // Configuramos el evento de click para el botón eliminar
        holder.btnEliminar.setOnClickListener(v -> {
            // Cambiar el estado a "Eliminada" en la base de datos
            DataNotificaciones dataNotificaciones = new DataNotificaciones(holder.itemView.getContext());
            dataNotificaciones.actualizarEstadoNotificacion(notificacion);

            // Eliminar la notificación de la lista
            notificaciones.remove(position);

            // Notificar al adaptador que se ha eliminado un elemento
            notifyItemRemoved(position);

            // Si el elemento no es el último, notificamos que la lista ha cambiado
            if (position < notificaciones.size()) {
                notifyItemRangeChanged(position, notificaciones.size());
            }
        });
    }



    @Override
    public int getItemCount() {
        return notificaciones.size();
    }

    public void setNotificaciones(List<Notificacion> notificaciones) {
        this.notificaciones = notificaciones;
        notifyDataSetChanged();
    }

    private void eliminarNotificacion(Notificacion notificacion) {
        // Aquí llamamos a la clase DataNotificaciones para actualizar el estado de la notificación
        DataNotificaciones dataNotificaciones = new DataNotificaciones();
        notificacion.setEstado("Eliminada");
        dataNotificaciones.actualizarEstadoNotificacion(notificacion);
    }

    public static class NotificacionViewHolder extends RecyclerView.ViewHolder {

        private TextView txtTipoNotificacion;
        private TextView txtFechaEnvio;
        private ImageButton btnEliminar;  // Añadir referencia al botón eliminar

        public NotificacionViewHolder(View itemView) {
            super(itemView);
            txtTipoNotificacion = itemView.findViewById(R.id.txt_tipo_notificacion);
            txtFechaEnvio = itemView.findViewById(R.id.txt_fecha_envio);
            btnEliminar = itemView.findViewById(R.id.btn_eliminar);  // Referenciar el botón
        }

        public void bind(Notificacion notificacion) {
            String tipoFormato = "";

            switch (notificacion.getTipoNotificacion()) {
                case Invitacion:
                    tipoFormato = "Invitación pendiente de aceptación de ";
                    break;
                case Confirmacion:
                    tipoFormato = "Invitación aceptada de ";
                    break;
                case Rechazo:
                    tipoFormato = "Invitación rechazada de ";
                    break;
            }

            txtTipoNotificacion.setText(tipoFormato + notificacion.getNombreRemitente());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String formattedDate = (notificacion.getFecha_envio() != null) ? notificacion.getFecha_envio().format(formatter) : "Fecha no disponible";
            txtFechaEnvio.setText("Fecha: " + formattedDate);
        }
    }
}

