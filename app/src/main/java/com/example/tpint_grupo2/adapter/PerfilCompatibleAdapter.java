package com.example.tpint_grupo2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tpint_grupo2.entidades.PerfilCompatible;
import com.example.tpint_grupo2.R;

import java.util.List;

public class PerfilCompatibleAdapter extends RecyclerView.Adapter<PerfilCompatibleAdapter.PerfilesViewHolder> {

    private List<PerfilCompatible> listaPerfiles;
    private int posicionSeleccionada = -1; // Variable para manejar el perfil seleccionado

    public PerfilCompatibleAdapter(List<PerfilCompatible> listaPerfiles) {
        this.listaPerfiles = listaPerfiles;
    }

    @Override
    public PerfilesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_perfil_compatible, parent, false);
        return new PerfilesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PerfilesViewHolder holder, int position) {
        PerfilCompatible perfil = listaPerfiles.get(position);

        // Concatenar nombre y apellido
        String nombreCompleto = perfil.getNombre() + " " + perfil.getApellido();

        // Asigna los datos a las vistas del item
        holder.txtNombreUsuario.setText(nombreCompleto);  // Nombre completo (nombre + apellido)

        // Mostrar "Masculino" si el sexo es "M", "Femenino" si es "F"
        if ("M".equals(perfil.getSexo())) {
            holder.txtSexo.setText("Masculino");
        } else if ("F".equals(perfil.getSexo())) {
            holder.txtSexo.setText("Femenino");
        }
        holder.txtEmailUsuario.setText(perfil.getEmail());
        holder.txtActividadesPreferidas.setText("Actividades preferidas: " + perfil.getTituloActividad());
        holder.txt_dias_disponibles.setText("Días Disponibles: " + perfil.getDiasDisponibles());
        holder.txt_horarios_disponibles.setText("Horarios Disponibles: " + perfil.getHorariosDisponibles());

        // Si el RadioButton está seleccionado, cambiar la posición seleccionada
        holder.radioButtonPerfil.setChecked(holder.getAdapterPosition() == posicionSeleccionada);

        // Listener para el RadioButton (cuando se cambia la selección)
        holder.radioButtonPerfil.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Usamos getAdapterPosition() para obtener la posición actual de manera segura
            int selectedPosition = holder.getAdapterPosition();

            if (isChecked) {
                // Si se selecciona este perfil, actualizamos la posición seleccionada
                if (posicionSeleccionada != selectedPosition) {
                    posicionSeleccionada = selectedPosition;
                    notifyDataSetChanged();  // Solo notificamos para actualizar la vista
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaPerfiles != null ? listaPerfiles.size() : 0;
    }

    // Método para obtener el perfil seleccionado
    public PerfilCompatible getPerfilSeleccionado() {
        if (posicionSeleccionada != -1 && listaPerfiles != null && posicionSeleccionada < listaPerfiles.size()) {
            return listaPerfiles.get(posicionSeleccionada);
        }
        return null;
    }

    // Método para actualizar la lista de perfiles
    public void setPerfiles(List<PerfilCompatible> perfiles) {
        this.listaPerfiles = perfiles;
        notifyDataSetChanged(); // Notificar que los datos han cambiado
    }

    public class PerfilesViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombreUsuario, txtEmailUsuario, txtSexo, txtActividadesPreferidas, txt_dias_disponibles, txt_horarios_disponibles;
        ImageView imgPerfil;
        RadioButton radioButtonPerfil;  // Usamos el RadioButton

        public PerfilesViewHolder(View itemView) {
            super(itemView);
            txtNombreUsuario = itemView.findViewById(R.id.txt_nombre_usuario);
            txtEmailUsuario = itemView.findViewById(R.id.txt_email_usuario);
            txtSexo = itemView.findViewById(R.id.txt_sexo);
            txtActividadesPreferidas = itemView.findViewById(R.id.txt_actividades_preferidas);
            txt_dias_disponibles = itemView.findViewById(R.id.txt_dias_disponibles);
            txt_horarios_disponibles = itemView.findViewById(R.id.txt_horarios_disponibles);
            imgPerfil = itemView.findViewById(R.id.img_perfil);
            radioButtonPerfil = itemView.findViewById(R.id.radio_button_perfil);  // Referencia al RadioButton
        }
    }
}
