package com.example.tpint_grupo2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tpint_grupo2.R;
import com.example.tpint_grupo2.entidades.Categoria;

import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;

public class CategoriasAdapter extends RecyclerView.Adapter<CategoriasAdapter.CategoriaViewHolder> {
    private List<Categoria> categorias;
    private StringBuilder categoriasSeleccionadasStringBuilder = new StringBuilder();

    private List<Integer> categoriasSeleccionadasIds = new ArrayList<>();

    public CategoriasAdapter(List<Categoria> categorias) {
        this.categorias = categorias;
    }

    @NonNull
    @Override
    public CategoriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categoria, parent, false);
        return new CategoriaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriaViewHolder holder, int position) {
        Categoria categoria = categorias.get(position);
        holder.checkBox.setText(categoria.getTitulo());

        holder.checkBox.setChecked(categoriasSeleccionadasIds.contains(categoria.getIdCategoria()));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                categoriasSeleccionadasIds.add(categoria.getIdCategoria());
            } else {
                categoriasSeleccionadasIds.remove(Integer.valueOf(categoria.getIdCategoria()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return categorias.size();
    }

    public static class CategoriaViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        public CategoriaViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox_categoria);
        }
    }

    public void setCategoriasSeleccionadas(List<Categoria> categoriasSeleccionadas) {
        categoriasSeleccionadasIds.clear();

        for (Categoria categoria : categoriasSeleccionadas) {
            categoriasSeleccionadasIds.add(categoria.getIdCategoria());
        }

        notifyDataSetChanged();
    }

    public String getCategoriasSeleccionadas() {
        categoriasSeleccionadasStringBuilder.setLength(0);

        for (Integer id : categoriasSeleccionadasIds) {
            if (categoriasSeleccionadasStringBuilder.length() > 0) {
                categoriasSeleccionadasStringBuilder.append(",");
            }
            categoriasSeleccionadasStringBuilder.append(id);
        }

        return categoriasSeleccionadasStringBuilder.toString();
    }
}