package com.example.tpint_grupo2;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.tpint_grupo2.conexion.DataActividadesAdmin;
import com.example.tpint_grupo2.conexion.DataLocalidades;
import com.example.tpint_grupo2.conexion.interfacesAdmin.UpdateActCallback;
import com.example.tpint_grupo2.elementos.cargandoDialog;
import com.example.tpint_grupo2.entidades.Actividad;
import com.example.tpint_grupo2.entidades.Categoria;
import com.example.tpint_grupo2.entidades.Localidad;
import com.example.tpint_grupo2.enums.EnumDias;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class FragmentModificarActividad extends Fragment {

    private ArrayList<Categoria> listaCategorias = new ArrayList<>();
    private ArrayList<Localidad> listaLocalidades = new ArrayList<>();
    private ArrayList<EnumDias> diasSeleccionados;
    private Button btnVolver, btnModificar;
    private DataActividadesAdmin dataActividades;
    private EditText tituloET;
    private Spinner spinCategoria, spinLocalidad;
    private TextView tv_act_categoria, tv_act_localidad;
    private CheckBox lunes,martes,miercoles,jueves,viernes,sabado,domingo;
    private TimePicker hora_inicio, hora_fin;
    private int actividadId;

    private RadioButton rb_libre_dias,rb_dias;
    private RadioButton rb_libre_horario,rb_horario;
    private GridLayout diasGroup;

    public FragmentModificarActividad() {}

    public static FragmentModificarActividad newInstance() {
        FragmentModificarActividad fragment = new FragmentModificarActividad();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_modificar_actividad, container, false);

        dataActividades = new DataActividadesAdmin();
        btnVolver = view.findViewById(R.id.btn_volver_actividad);
        btnModificar = view.findViewById(R.id.btn_modificar_actividad);
        tituloET = view.findViewById(R.id.edt_nombre_actividad);
        spinCategoria = view.findViewById(R.id.spin_categoria);
        spinLocalidad = view.findViewById(R.id.spin_localidad);
        tv_act_categoria = view.findViewById(R.id.tv_actual_categoria);
        tv_act_localidad = view.findViewById(R.id.tv_actual_localidad);
        lunes = view.findViewById(R.id.chk_lunes);
        martes = view.findViewById(R.id.chk_martes);
        miercoles = view.findViewById(R.id.chk_miercoles);
        jueves = view.findViewById(R.id.chk_jueves);
        viernes = view.findViewById(R.id.chk_viernes);
        sabado = view.findViewById(R.id.chk_sabado);
        domingo = view.findViewById(R.id.chk_domingo);
        hora_inicio = view.findViewById(R.id.hora_inicio);
        hora_fin = view.findViewById(R.id.hora_fin);
        hora_inicio.setIs24HourView(true);
        hora_fin.setIs24HourView(true);

        //Horario Predeterminado
        hora_inicio.setHour(14);
        hora_inicio.setMinute(0);
        hora_fin.setHour(15);
        hora_fin.setMinute(0);

        //RadioButtons y Grupo de CheckBox
        rb_libre_dias=view.findViewById(R.id.rb_libre);
        rb_dias=view.findViewById(R.id.rb_dias);
        rb_libre_horario=view.findViewById(R.id.rb_libre_horario);
        rb_horario=view.findViewById(R.id.rb_horario);
        diasGroup=view.findViewById(R.id.dias_group);


        cargarSpinners(dataActividades);

        if (getArguments() != null) {
            String actividadIdString = getArguments().getString("actividad_id");
            try {
                actividadId = Integer.parseInt(actividadIdString);
                cargarActividad(dataActividades, actividadId);

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        //Eventos Click
        rb_libre_dias.setOnClickListener(this::selectedLibreDias);
        rb_dias.setOnClickListener(this::selectedSeleccionarDias);
        btnVolver.setOnClickListener(v -> volverALaVistaAnterior());
        btnModificar.setOnClickListener(v -> modificarLaActividad(dataActividades, actividadId));

        return view;
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
                spinLocalidad.setAdapter(localidadAdapter);
            }
        });
    }


    public void cargarActividad(DataActividadesAdmin data, int actividadId) {
        cargandoDialog cargando=new cargandoDialog(getActivity());
        cargando.mostrarCargandoMensaje();

        Future<Actividad> future = data.buscarActividadPorID(actividadId);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Actividad actividad = future.get();

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (actividad != null) {
                                    tituloET.setText(actividad.getTitulo());
                                    tv_act_categoria.setText(actividad.getCategoria().getTitulo());
                                    tv_act_localidad.setText((actividad.getLocalidad().getNombre()));

                                    for (int i = 0; i < listaCategorias.size(); i++) {
                                        if (listaCategorias.get(i).getIdCategoria() == actividad.getCategoria().getIdCategoria()) {
                                            spinCategoria.setSelection(i);
                                            break;
                                        }
                                    }

                                    for (int i = 0; i < listaLocalidades.size(); i++) {
                                        if (listaLocalidades.get(i).getIdLocalidad() == actividad.getLocalidad().getIdLocalidad()) {
                                            spinLocalidad.setSelection(i);
                                            break;
                                        }
                                    }

                                    diasSeleccionados = actividad.getDias_dictados();

                                    if(diasSeleccionados.contains(EnumDias.LIBRE)){
                                        rb_libre_dias.setChecked(true);

                                        for (int i = 0; i < diasGroup.getChildCount(); i++) {
                                            View child = diasGroup.getChildAt(i);
                                            if (child instanceof CheckBox) {
                                                child.setEnabled(false);
                                            }
                                        }


                                    } else {

                                        rb_dias.setChecked(true);
                                        lunes.setChecked(diasSeleccionados.contains(EnumDias.Lunes));
                                        martes.setChecked(diasSeleccionados.contains(EnumDias.Martes));
                                        miercoles.setChecked(diasSeleccionados.contains(EnumDias.Miercoles));
                                        jueves.setChecked(diasSeleccionados.contains(EnumDias.Jueves));
                                        viernes.setChecked(diasSeleccionados.contains(EnumDias.Viernes));
                                        sabado.setChecked(diasSeleccionados.contains(EnumDias.Sabado));
                                        domingo.setChecked(diasSeleccionados.contains(EnumDias.Domingo));

                                        //Inhabilitamos horario libre
                                        rb_libre_horario.setEnabled(false);
                                    }

                                    String horario = actividad.getHorario();
                                    if (horario != null && !horario.isEmpty()) {
                                        if (horario.equals("LIBRE")){
                                            rb_libre_horario.setChecked(true);

                                            //Inhabilitamos horario del timepicker
                                            rb_horario.setEnabled(false);
                                            hora_inicio.setEnabled(false);
                                            hora_fin.setEnabled(false);


                                        } else{
                                            rb_horario.setChecked(true);
                                            String[] horas = horario.split(" - ");
                                            if (horas.length == 2) {
                                                String[] horaInicio = horas[0].split(":");
                                                String[] horaFin = horas[1].split(":");

                                                hora_inicio.setHour(Integer.parseInt(horaInicio[0]));
                                                hora_inicio.setMinute(Integer.parseInt(horaInicio[1]));

                                                hora_fin.setHour(Integer.parseInt(horaFin[0]));
                                                hora_fin.setMinute(Integer.parseInt(horaFin[1]));
                                            }
                                        }
                                    }

                                } else {
                                    Log.e("Actividad", "No se encontró la actividad.");
                                }
                            }
                        });
                    }
                } catch (InterruptedException | ExecutionException e) {
                    Log.e("CargarActividad", "Error al cargar la actividad", e);
                }
            }
        }).start();

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                   cargando.cerrarDialogo();
                                }
                            }

                , 5000);
    }



    private void modificarLaActividad(DataActividadesAdmin data, int actividadId) {
        Integer idCategoria = obtenerCategoriaSeleccionada();
        Integer idLocalidad = obtenerLocalidadSeleccionada();
        String nuevoTitulo = tituloET.getText().toString().trim();
        String horario;

        EnumSet<EnumDias> nuevosDias = EnumSet.noneOf(EnumDias.class);
        if(rb_dias.isChecked()) {
            if (lunes.isChecked()) nuevosDias.add(EnumDias.Lunes);
            if (martes.isChecked()) nuevosDias.add(EnumDias.Martes);
            if (miercoles.isChecked()) nuevosDias.add(EnumDias.Miercoles);
            if (jueves.isChecked()) nuevosDias.add(EnumDias.Jueves);
            if (viernes.isChecked()) nuevosDias.add(EnumDias.Viernes);
            if (sabado.isChecked()) nuevosDias.add(EnumDias.Sabado);
            if (domingo.isChecked()) nuevosDias.add(EnumDias.Domingo);
        }else{
            nuevosDias.add(EnumDias.LIBRE);
        }

        String diasDictados = nuevosDias.stream()
                .map(EnumDias::name)
                .collect(Collectors.joining("-"));

        if(rb_horario.isChecked()) {

            int horaI = hora_inicio.getHour();
            int minutoI = hora_inicio.getMinute();
            int horaF = hora_fin.getHour();
            int minutoF = hora_fin.getMinute();

            horario = String.format("%02d:%02d - %02d:%02d", horaI, minutoI, horaF, minutoF);

            if (horaI > horaF || (horaI == horaF && minutoI >= minutoF)) {
                Toast.makeText(getContext(), "El horario de inicio debe ser anterior al horario de fin", Toast.LENGTH_SHORT).show();
                return;
            }
        }else {
            horario="LIBRE";
        }

        if (nuevoTitulo.isEmpty()) {
            tituloET.setError("El nombre de la actividad es obligatorio");
            tituloET.requestFocus();
            return;
        }

        if (diasDictados.isEmpty()) {
            Toast.makeText(getContext(),"Los días dictados son obligatorios",Toast.LENGTH_LONG).show();
            return;
        }

        data.actualizarActividad(actividadId, idCategoria, idLocalidad, nuevoTitulo, diasDictados, horario, new UpdateActCallback() {
            @Override
            public void onUpdateSuccess() {
                Toast.makeText(getActivity(), "Actividad actualizada correctamente.", Toast.LENGTH_SHORT).show();

                FragmentManager fragmentManager = getParentFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack();
                } else {
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                }
            }

            @Override
            public void onUpdateError(String errorMessage) {
                Toast.makeText(getActivity(), "Error al actualizar actividad: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });

    }

    private void volverALaVistaAnterior() {
        FragmentManager fragmentManager = getParentFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        }
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
            int position = spinLocalidad.getSelectedItemPosition();
            if (position >= 0 && position < listaLocalidades.size()) {
                return listaLocalidades.get(position).getIdLocalidad();
            }
        }
        return -1;
    }

    public void selectedLibreDias(View view){
        if(rb_libre_dias.isChecked()){
            for (int i = 0; i < diasGroup.getChildCount(); i++) {
                View child = diasGroup.getChildAt(i);
                if (child instanceof CheckBox) {
                    child.setEnabled(false);
                }
            }
            rb_libre_dias.setEnabled(true);
            rb_libre_dias.setChecked(true);
            rb_libre_horario.setChecked(true);
            rb_libre_horario.setEnabled(true);
            rb_horario.setEnabled(false);
            hora_fin.setEnabled(false);
            hora_inicio.setEnabled(false);
        }

    }

    public void selectedSeleccionarDias(View view){
        if(rb_dias.isChecked()){
            for (int i = 0; i < diasGroup.getChildCount(); i++) {
                View child = diasGroup.getChildAt(i);
                if (child instanceof CheckBox) {
                    child.setEnabled(true);
                }
            }
            rb_horario.setChecked(true);
            rb_horario.setEnabled(true);
            rb_libre_horario.setEnabled(false);
            hora_fin.setEnabled(true);
            hora_inicio.setEnabled(true);
        }

    }

}