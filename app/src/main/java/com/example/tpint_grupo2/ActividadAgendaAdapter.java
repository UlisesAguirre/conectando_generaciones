package com.example.tpint_grupo2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.example.tpint_grupo2.conexion.DataMisActividades;
import com.example.tpint_grupo2.conexion.DataMisSolicitudes;
import com.example.tpint_grupo2.entidades.ActividadAgenda;
import com.example.tpint_grupo2.entidades.ActividadCoordinada;
import com.example.tpint_grupo2.entidades.Usuario;
import com.example.tpint_grupo2.enums.EnumEstadoPresenciaUsuario;
import com.example.tpint_grupo2.enums.EnumEstadosActividades;
import com.example.tpint_grupo2.enums.EnumTiposUsuario;

public class ActividadAgendaAdapter extends RecyclerView.Adapter<ActividadAgendaAdapter.ActividadAgendaViewHolder> {

    private final List<ActividadCoordinada> actividades;

    public ActividadAgendaAdapter(List<ActividadCoordinada> actividades) {
        this.actividades = actividades;
    }


    @NonNull
    @Override
    public ActividadAgendaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_actividad_agenda, parent, false);
        return new ActividadAgendaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActividadAgendaViewHolder holder, int position) {
        ActividadCoordinada actividad = actividades.get(position);
        holder.tvNombre.setText("Nombre: " + actividad.getActividad().getTitulo());
        holder.tvFecha.setText("Fecha: " + actividad.getFechaParticipar());
        holder.tvOrganizador.setText("Organizador: " + actividad.getVoluntario().getNombre() +
                " " + actividad.getVoluntario().getApellido());

        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd"); // Define el formato
        Date fechaDate = null;

        Date fechaActual = new Date(); // Obtiene la fecha y hora actual

        try {
            fechaActual = formato.parse(formato.format(fechaActual));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        try {
            fechaDate = formato.parse(actividad.getFechaParticipar());
            System.out.println("Fecha convertida: " + fechaDate);
        } catch (ParseException e) {
            e.printStackTrace(); // Maneja el error si el formato no coincide
        }

        if (!fechaDate.before(fechaActual)) {
            holder.btnNoAsistire.setVisibility(View.GONE);
            holder.btnAsistire.setVisibility(View.GONE);
            holder.tv_no_califica.setVisibility(View.VISIBLE);
        } else {
            holder.btnNoAsistire.setVisibility(View.VISIBLE);
            holder.btnAsistire.setVisibility(View.VISIBLE);
            holder.tv_no_califica.setVisibility(View.GONE);
        }

        DataMisActividades service = new DataMisActividades();

        holder.btnAsistire.setOnClickListener(v -> {
            mostrarDialogoConfirmacion(v, "¿Estás seguro de marcar su ASISTENCIA a esta actividad?",
                    EnumEstadosActividades.ASISTIO,
                    actividad.getIdActividadCoordinada(),
                    position,
                    actividad.getAdultoMayor().getNro_documento(),
                    EnumEstadoPresenciaUsuario.PRESENTE,
                    actividad.getVoluntario().getTipo_usuario());
        });

        holder.btnNoAsistire.setOnClickListener(v -> {
            mostrarDialogoConfirmacion(v, "¿Esta seguro de marcar su AUSENCIA a esta actividad?",
                    EnumEstadosActividades.NO_ASISTIO,
                    actividad.getIdActividadCoordinada(),
                    position,
                    actividad.getAdultoMayor().getNro_documento(),
                    EnumEstadoPresenciaUsuario.AUSENTE,
                    actividad.getVoluntario().getTipo_usuario());
        });
    }

    private void mostrarDialogoConfirmacion(View view, String mensaje, EnumEstadosActividades estado, int idActividad, int position, int nro_documento, EnumEstadoPresenciaUsuario estadoPresencia, EnumTiposUsuario tipoUsuario) {
        new AlertDialog.Builder(view.getContext())
                .setMessage(mensaje)
                .setCancelable(false)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        DataMisSolicitudes service = new DataMisSolicitudes();
                        DataMisActividades serviceActividades = new DataMisActividades();
                        serviceActividades.UpdateEstadoActividad(estado, idActividad, tipoUsuario);
                        serviceActividades.UpdateEstadoPresenciaUsuario(estadoPresencia, idActividad, nro_documento);


                        actividades.remove(position);

                        notifyItemRemoved(position);

                       String mensajeConfirmacion = estado == EnumEstadosActividades.ASISTIO ?
                                "Comfirmamos su asistencia a la actividad" : "Confirmamos su NO asistencia a la actividad";

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
        return actividades.size();
    }

    public static class ActividadAgendaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvFecha, tvOrganizador, tvEstado, tvIntCalificacion, tv_no_califica;
        Button btnAsistire, btnNoAsistire;
        LinearLayout linear_botones;

        public ActividadAgendaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvOrganizador = itemView.findViewById(R.id.tvOrganizador);
            btnAsistire = itemView.findViewById(R.id.btnAsistire);
            btnNoAsistire = itemView.findViewById(R.id.btnNoAsistire);
            linear_botones = itemView.findViewById(R.id.linear_botones);
            tv_no_califica = itemView.findViewById(R.id.tv_no_se_califica);
        }
    }

}