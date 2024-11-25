package com.example.tpint_grupo2;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tpint_grupo2.conexion.DataActividadesAdmin;
import com.example.tpint_grupo2.conexion.DataLocalidades;
import com.example.tpint_grupo2.conexion.interfacesAdmin.BuscarActCallback;
import com.example.tpint_grupo2.entidades.Actividad;
import com.example.tpint_grupo2.entidades.Categoria;
import com.example.tpint_grupo2.entidades.Localidad;
import com.example.tpint_grupo2.enums.EnumDias;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FragmentAdministrarActividades extends Fragment {

    private ArrayList<Categoria> listaCategorias = new ArrayList<>();
    private ArrayList<Localidad> listaLocalidades = new ArrayList<>();
    private String[] dias = {"Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo"};
    private DataActividadesAdmin dataActividades;
    private Spinner spinCategoria, spinLocalizacion, spinDias;
    private EditText nombreActividad;
    private Button btnFiltros;
    private CheckBox boxCategoria, boxLocalizacion, boxDias;

    public FragmentAdministrarActividades() {}

    public static FragmentAdministrarActividades newInstance() {
        FragmentAdministrarActividades fragment = new FragmentAdministrarActividades();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_administrar_actividades, container, false);

        dataActividades = new DataActividadesAdmin();
        spinCategoria = view.findViewById(R.id.spin_categoria);
        spinLocalizacion = view.findViewById(R.id.spin_localizacion);
        spinDias = view.findViewById(R.id.spin_dias);
        nombreActividad = view.findViewById(R.id.edt_nombre_actividad);
        btnFiltros = view.findViewById(R.id.btn_aplicar_filtros);
        boxCategoria = view.findViewById(R.id.chkCategoria);
        boxLocalizacion = view.findViewById(R.id.chkLocalizacion);
        boxDias = view.findViewById(R.id.chkDias);


        boxDias.setChecked(true);
        boxCategoria.setChecked(true);
        boxLocalizacion.setChecked(true);
        habilitarCategoria();
        habilitarDias();
        habilitarLocalizacion();

        cargarSpinners(dataActividades);

        btnFiltros.setOnClickListener(v -> {
            aplicarFiltros(dataActividades);
        });

        boxCategoria.setOnClickListener(v -> {
            habilitarCategoria();
        });
        boxLocalizacion.setOnClickListener(v -> {
            habilitarLocalizacion();
        });
        boxDias.setOnClickListener(v -> {
            habilitarDias();
        });

        return view;
    }

    private void aplicarFiltros(DataActividadesAdmin data) {
        String nombreActividadInicial = nombreActividad.getText().toString().trim();
        String nombreAct = nombreActividadInicial.isEmpty() ? null : nombreActividadInicial;
        String dia = boxDias.isChecked() ? null : obtenerDiaSeleccionado();
        Integer idCategoria = boxCategoria.isChecked() ? null : obtenerCategoriaSeleccionada();
        Integer idLocalidad = boxLocalizacion.isChecked() ? null : obtenerLocalidadSeleccionada();

        data.buscarActividades(idCategoria, idLocalidad, nombreAct, dia, new BuscarActCallback() {
            @Override
            public void onBuscarSuccess(List<Actividad> actividades) {
                mostrarActividades(actividades);
            }

            @Override
            public void onBuscarError(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        //Reseteo de controles de busqueda
        nombreActividad.setText("");
        boxDias.setChecked(true);
        boxCategoria.setChecked(true);
        boxLocalizacion.setChecked(true);
        habilitarCategoria();
        habilitarDias();
        habilitarLocalizacion();

    }

    private void mostrarActividades(List<Actividad> actividades) {
        LinearLayout container = getView().findViewById(R.id.linearLayoutContainer);

        container.removeAllViews();

        for (Actividad actividad : actividades) {
            View cardView = LayoutInflater.from(getContext()).inflate(R.layout.card_template, container, false);

            TextView textNombre = cardView.findViewById(R.id.text_nombre);
            TextView textCategoria = cardView.findViewById(R.id.text_categoria);
            TextView textDia = cardView.findViewById(R.id.text_dia);
            TextView textHorario = cardView.findViewById(R.id.text_horario);

            textNombre.setText(actividad.getTitulo());
            textCategoria.setText(actividad.getCategoria().getTitulo());
            textDia.setText(actividad.getDias_dictados().toString());
            textHorario.setText(actividad.getHorario());

            Button buttonEditar = cardView.findViewById(R.id.button_editar);
            Button buttonBorrar = cardView.findViewById(R.id.button_borrar);

            buttonEditar.setOnClickListener(v -> editarActividad(actividad));
            buttonBorrar.setOnClickListener(v -> borrarActividad(actividad));

            cardView.setVisibility(View.VISIBLE);

            container.addView(cardView);
        }
    }

    private void editarActividad(Actividad actividad) {
        FragmentModificarActividad fragmentModificar = new FragmentModificarActividad();

        Bundle args = new Bundle();
        args.putString("actividad_id", String.valueOf(actividad.getIdActividad()));
        fragmentModificar.setArguments(args);

        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainerView2, fragmentModificar);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void borrarActividad(Actividad actividad) {
        FragmentEliminarActividad fragmentEliminar = new FragmentEliminarActividad();

        Bundle args = new Bundle();
        args.putString("actividad_id", String.valueOf(actividad.getIdActividad()));
        args.putString("actividad_nombre", actividad.getTitulo());

        fragmentEliminar.setArguments(args);
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainerView2, fragmentEliminar);
        transaction.addToBackStack(null);
        transaction.commit();
    }



    private void habilitarDias() {
        spinDias.setEnabled(!boxDias.isChecked());
    }

    private void habilitarLocalizacion() {spinLocalizacion.setEnabled(!boxLocalizacion.isChecked());}

    private void habilitarCategoria() {spinCategoria.setEnabled(!boxCategoria.isChecked());}

    private String obtenerDiaSeleccionado() {
        return spinDias.getSelectedItem().toString();
    }

    private int obtenerCategoriaSeleccionada() {
        if (listaCategorias != null && !listaCategorias.isEmpty()) {
            int position = spinCategoria.getSelectedItemPosition();
            if (position >= 0 && position < listaCategorias.size()) {
                return listaCategorias.get(position).getIdCategoria();
            }
        }
        return -1;
    }

    private int obtenerLocalidadSeleccionada() {
        if (listaLocalidades != null && !listaLocalidades.isEmpty()) {
            int position = spinLocalizacion.getSelectedItemPosition();
            if (position >= 0 && position < listaLocalidades.size()) {
                return listaLocalidades.get(position).getIdLocalidad();
            }
        }
        return -1;
    }

    private void cargarSpinners(DataActividadesAdmin data) {
        // Cargar categorías
        data.obtenerCategorias(new DataActividadesAdmin.OnDataLoadedCallback() {
            @Override
            public void onDataLoaded(ArrayList<?> dataList) {
                listaCategorias = (ArrayList<Categoria>) dataList;
                ArrayAdapter<Categoria> categoriaAdapter = new ArrayAdapter<Categoria>(getContext(), android.R.layout.simple_spinner_item, listaCategorias) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        TextView textView = (TextView) super.getView(position, convertView, parent);
                        textView.setText(listaCategorias.get(position).getTitulo());
                        return textView;
                    }

                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                        textView.setText(listaCategorias.get(position).getTitulo());
                        return textView;
                    }
                };
                categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinCategoria.setAdapter(categoriaAdapter);
            }
        });

        // Obtener todas las localidades y cargar en el spinner
        new DataLocalidades(getContext()).obtenerTodasLasLocalidades(new DataLocalidades.OnLocalidadesRetrievedCallback() {
            @Override
            public void onLocalidadesRetrieved(List<Localidad> localidades) {
                listaLocalidades = new ArrayList<>(localidades);
                ArrayAdapter<Localidad> localidadAdapter = new ArrayAdapter<Localidad>(getContext(), android.R.layout.simple_spinner_item, listaLocalidades) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        TextView textView = (TextView) super.getView(position, convertView, parent);
                        textView.setText(listaLocalidades.get(position).getNombre());
                        return textView;
                    }

                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                        textView.setText(listaLocalidades.get(position).getNombre());
                        return textView;
                    }
                };
                localidadAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinLocalizacion.setAdapter(localidadAdapter);
            }
        });

        // Cargar los días en el spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, dias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinDias.setAdapter(adapter);
    }
}