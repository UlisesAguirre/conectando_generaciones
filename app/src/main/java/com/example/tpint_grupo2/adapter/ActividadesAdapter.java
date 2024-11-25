package com.example.tpint_grupo2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tpint_grupo2.R;
import com.example.tpint_grupo2.entidades.Actividad;
import com.example.tpint_grupo2.entidades.Categoria;

import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;

public class ActividadesAdapter extends RecyclerView.Adapter<ActividadesAdapter.ActividadViewHolder> {
    private List<Actividad> actividades;
    private StringBuilder actividadesSeleccionadas = new StringBuilder();

    public ActividadesAdapter(List<Actividad> actividades) {
        this.actividades = actividades;
    }

    @NonNull
    @Override
    public ActividadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_actividad, parent, false);
        return new ActividadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActividadViewHolder holder, int position) {
        Actividad actividad = actividades.get(position);
        holder.checkBox.setText(actividad.getTitulo());
        holder.checkBox.setTag(actividad.getCategoria().getIdCategoria());

        holder.checkBox.setChecked(false);

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String idActividad = String.valueOf(buttonView.getTag());

            if (isChecked) {
                if (actividadesSeleccionadas.length() > 0) {
                    actividadesSeleccionadas.append(",");
                }
                actividadesSeleccionadas.append(idActividad);
            } else {
                String currentSelection = actividadesSeleccionadas.toString();
                if (currentSelection.contains(idActividad)) {
                    actividadesSeleccionadas = new StringBuilder(currentSelection.replace(idActividad + ",", ""));
                    if (actividadesSeleccionadas.toString().endsWith(",")) {
                        actividadesSeleccionadas.deleteCharAt(actividadesSeleccionadas.length() - 1);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return actividades.size();
    }

    public String getActividadesSeleccionadas() {
        return actividadesSeleccionadas.toString();
    }

    public static class ActividadViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        public ActividadViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox_actividad);
        }
    }

    public void setActividadesSeleccionadas(ArrayList<Categoria> categoriasSeleccionadas) {
        actividadesSeleccionadas.setLength(0);

        for (Categoria categoria : categoriasSeleccionadas) {
            if (actividadesSeleccionadas.length() > 0) {
                actividadesSeleccionadas.append(",");
            }
            actividadesSeleccionadas.append(String.valueOf(categoria.getIdCategoria()));
        }

        notifyDataSetChanged();
    }
}