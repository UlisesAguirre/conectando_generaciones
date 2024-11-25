package com.example.tpint_grupo2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tpint_grupo2.conexion.DataActividadesACalificar;
import com.example.tpint_grupo2.conexion.DataMisSolicitudes;
import com.example.tpint_grupo2.entidades.PresenciaUsuarios;
import com.example.tpint_grupo2.enums.EnumEstadosActividades;
import com.example.tpint_grupo2.enums.EnumTiposUsuario;

import java.util.ArrayList;
import java.util.List;



public class ActividadPasadaAdapter extends RecyclerView.Adapter<ActividadPasadaAdapter.ActividadPasadaViewHolder> {

    private List<PresenciaUsuarios> actividades;
    private Boolean calificar;
    private Fragment parentFragment;

    public ActividadPasadaAdapter(ArrayList<PresenciaUsuarios> actividadesAgenda, Fragment parentFragment,
                                  Boolean calificar) {
        this.actividades = actividadesAgenda;
        this.parentFragment = parentFragment;
        this.calificar = calificar;
    }


    @NonNull
    @Override
    public ActividadPasadaAdapter.ActividadPasadaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_actividad_pasada_agenda, parent, false);
        return new ActividadPasadaAdapter.ActividadPasadaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActividadPasadaAdapter.ActividadPasadaViewHolder holder, int position) {
        PresenciaUsuarios actividad = actividades.get(position);
        holder.tvNombre.setText("Nombre: " + actividad.getActividad().getActividad().getTitulo());
        holder.tvFecha.setText("Fecha: " + actividad.getActividad().getFechaParticipar());
        holder.tvOrganizador.setText("Organizador: " + actividad.getActividad().getVoluntario().getNombre() +
                                    " " + actividad.getActividad().getVoluntario().getApellido());
        holder.tvEstado.setText("Estado: " + actividad.getActividad().getEstadoActividad());


        if (calificar) {
            holder.btnCalificar.setVisibility(View.VISIBLE);
            holder.btnNoCalificar.setVisibility(View.VISIBLE);
            holder.tvEstado.setVisibility(View.GONE);
            holder.rbCalificacion.setVisibility(View.GONE);
            holder.tvComentario.setVisibility(View.GONE);
            holder.tvCalificacion.setVisibility(View.GONE);
            holder.tv_comentario_title.setVisibility(View.GONE);
        } else {
            holder.btnCalificar.setVisibility(View.GONE);
            holder.btnNoCalificar.setVisibility(View.GONE);
            holder.tvEstado.setVisibility(View.VISIBLE);

        }

        if ( actividad.getActividad().getEstadoActividad() == EnumEstadosActividades.CALIFICADA) {
                holder.tvComentario.setText("\"" + actividad.getOpinion() + "\"");
                holder.rbCalificacion.setRating(actividad.getCalificacion());
        } else {
            holder.rbCalificacion.setVisibility(View.GONE);
            holder.tvComentario.setVisibility(View.GONE);
            holder.tvCalificacion.setVisibility(View.GONE);
            holder.tv_comentario_title.setVisibility(View.GONE);
        }


        holder.btnCalificar.setOnClickListener(view -> {
            // Usar el Fragmento padre para la transacción
            CalificarActividadFragment objCalificarActividad_frag = new CalificarActividadFragment();

            Bundle bundle = new Bundle();
            bundle.putInt("idActividad", actividad.getActividad().getIdActividadCoordinada());
            bundle.putString("nombreActividad", actividad.getActividad().getActividad().getTitulo());
            bundle.putString("fechaActividad", actividad.getActividad().getFechaParticipar());
            bundle.putString("organizador", actividad.getActividad().getVoluntario().getNombre() + " "
                            + actividad.getActividad().getVoluntario().getApellido());


            objCalificarActividad_frag.setArguments(bundle);


            parentFragment.getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_menu_principal_container, objCalificarActividad_frag)
                    .addToBackStack(null)
                    .commit();

        });

        holder.btnNoCalificar.setOnClickListener(v -> {
            mostrarDialogoConfirmacion(v, "¿Estás seguro de no calificar la actividad? - Luego no podra calificarla",
                    actividad.getActividad().getIdActividadCoordinada(),
                    position,
                    actividad.getActividad().getVoluntario().getTipo_usuario());
        });
    }

    private void mostrarDialogoConfirmacion(View view, String mensaje, int idActividad, int position, EnumTiposUsuario tipoUsuario) {
        new AlertDialog.Builder(view.getContext())
                .setMessage(mensaje)
                .setCancelable(false)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        DataActividadesACalificar service = new DataActividadesACalificar();
                        service.UpdateEstadoActividad2(EnumEstadosActividades.NO_CALIFICADA, idActividad, tipoUsuario);

                        actividades.remove(position);

                        notifyItemRemoved(position);



                        new AlertDialog.Builder(view.getContext())
                                .setMessage("Actividad no calificada")
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

    public static class ActividadPasadaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvFecha, tvOrganizador, tvEstado, tvComentario, tvCalificacion, tv_comentario_title;
        RatingBar rbCalificacion;
        Button btnCalificar;
        Button btnNoCalificar;

        public ActividadPasadaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            tvOrganizador = itemView.findViewById(R.id.tvOrganizador);
            btnCalificar = itemView.findViewById(R.id.btnCalificar);
            btnNoCalificar = itemView.findViewById(R.id.btnNoCalificar);
            tvComentario = itemView.findViewById(R.id.tv_comentario);
            rbCalificacion = itemView.findViewById(R.id.rating_bar_calificacion);
            tvCalificacion = itemView.findViewById(R.id.tvCalificacion);
            tv_comentario_title = itemView.findViewById(R.id.tv_comentario_title);

        }
    }

}