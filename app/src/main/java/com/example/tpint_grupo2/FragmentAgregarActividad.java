package com.example.tpint_grupo2;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.tpint_grupo2.conexion.DataActividadesAdmin;
import com.example.tpint_grupo2.conexion.DataLocalidades;
import com.example.tpint_grupo2.conexion.interfacesAdmin.InsertActCallback;
import com.example.tpint_grupo2.entidades.Categoria;
import com.example.tpint_grupo2.entidades.Localidad;
import com.example.tpint_grupo2.enums.EnumDias;

import java.util.ArrayList;
import java.util.List;

public class FragmentAgregarActividad extends Fragment {
    private ArrayList<Categoria> listaCategorias = new ArrayList<>();
    private ArrayList<Localidad> listaLocalidades = new ArrayList<>();

    private EditText edtNombreActividad;
    private Spinner spinCategoria, spinLocalizacion;
    private TimePicker hora_i, hora_f;
    private ArrayList<EnumDias> multiDays;
    private DataActividadesAdmin dataActividades;
    private GridLayout diasGroup;

    private RadioButton rb_libre, rb_dias;
    private RadioButton rb_libre_horario, rb_horario;

    public FragmentAgregarActividad() {}

    public static FragmentAgregarActividad newInstance() {
        FragmentAgregarActividad fragment = new FragmentAgregarActividad();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agregar_actividad, container, false);

        // Inicializar el objeto de datos (si es necesario)
        dataActividades = new DataActividadesAdmin();

        // Recopila días seleccionados en formato 'dia-dia'
        diasGroup = view.findViewById(R.id.dias_disponibles_group);
        multiDays = new ArrayList<>();

        //otras vistas y elementos
        edtNombreActividad = view.findViewById(R.id.edt_nombre_actividad);
        spinCategoria = view.findViewById(R.id.spin_categoria);
        spinLocalizacion = view.findViewById(R.id.spin_localizacion);
        hora_i = view.findViewById(R.id.hora_inicio);
        hora_f = view.findViewById(R.id.hora_fin);
        hora_i.setIs24HourView(true);
        hora_f.setIs24HourView(true);

        //Seteo de horas por defecto
        hora_i.setHour(8);
        hora_i.setMinute(0);
        hora_f.setHour(12);
        hora_f.setMinute(0);

        //RadioButton Dias
        rb_dias=view.findViewById(R.id.rb_dias);
        rb_libre=view.findViewById(R.id.rb_libre);

        //RadioButton Horario
        rb_libre_horario=view.findViewById(R.id.rb_libre_horario);
        rb_horario=view.findViewById(R.id.rb_horario);

        //Deshabilitar ckBox dias
        estadoInicialRB();

        // Boton de guardar actividad
        Button btnGuardar = view.findViewById(R.id.btn_guardar_actividad);

        //cargar spinners
        cargarSpinners(dataActividades);

        //boton para guardar una nueva actividad
        btnGuardar.setOnClickListener(v -> {
            guardarActividad(dataActividades);
        });

        //Click para habilitar o deshabilitar días
        rb_libre.setOnClickListener(this::selectedLibreDias);
        rb_dias.setOnClickListener(this::selectedSeleccionarDias);

        return view;
    }

    private void guardarActividad(DataActividadesAdmin data) {
        // Obtengo la data de los controles
        String nombreActividad = edtNombreActividad.getText().toString().trim();


        String diasDictados; //Variable para campo días
        String horario; //Variable para campo horario

        // Validación para permitir solo letras en el nombre de la actividad
        if (!nombreActividad.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            edtNombreActividad.setError("El nombre de la actividad solo debe contener letras");
            edtNombreActividad.requestFocus();
            return;
        }

        // Obtener dias si se seleccionó el rb
        if(rb_dias.isChecked()) {
            for (int i = 0; i < diasGroup.getChildCount(); i++) {
                View child = diasGroup.getChildAt(i);
                if (child instanceof CheckBox) {
                    CheckBox diaCheckBox = (CheckBox) child;
                    if (diaCheckBox.isChecked()) {
                        String diaTexto = diaCheckBox.getText().toString();
                        EnumDias diaEnum = EnumDias.valueOf(diaTexto);
                        multiDays.add(diaEnum);
                    }
                }
            }

            diasDictados = obtenerDiasDisponiblesDeArray(multiDays);

            //Validacion
            if (diasDictados.isEmpty()) {
                Toast.makeText(getContext(),"Debe seleccionar al menos un día disponible",Toast.LENGTH_LONG).show();
                diasGroup.requestFocus();
                return;
            }
        }else{
            diasDictados="LIBRE";
        }

        Integer idCategoria = obtenerCategoriaSeleccionada();
        Integer idLocalidad = obtenerLocalidadSeleccionada();

        if(rb_horario.isChecked()){
            int horaInicio = hora_i.getHour();
            int minutoInicio = hora_i.getMinute();
            int horaFin = hora_f.getHour();
            int minutoFin = hora_f.getMinute();

            horario = String.format("%02d:%02d - %02d:%02d", horaInicio, minutoInicio, horaFin, minutoFin);

            //Validacion
            if (horaInicio > horaFin || (horaInicio == horaFin && minutoInicio >= minutoFin)) {
                Toast.makeText(getContext(), "El horario de inicio debe ser anterior al horario de fin", Toast.LENGTH_SHORT).show();
                return;
            }

        }else{
            horario="LIBRE";
        }

        // Validaciones
        if (nombreActividad.isEmpty()) {
            edtNombreActividad.setError("El nombre de la actividad es obligatorio");
            edtNombreActividad.requestFocus();
            return;
        }

        data.insertarActividad(idCategoria, idLocalidad, nombreActividad, diasDictados, horario, new InsertActCallback() {
            @Override
            public void onInsertSuccess() {
                Toast.makeText(getContext(), "Actividad guardada exitosamente", Toast.LENGTH_SHORT).show();
                limpiarCampos();
            }

            @Override
            public void onInsertError(String errorMessage) {
                Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void limpiarCampos() {
        edtNombreActividad.setText("");
        multiDays.clear();
        for (int i = 0; i < diasGroup.getChildCount(); i++) {
            View child = diasGroup.getChildAt(i);
            if (child instanceof CheckBox) {
                CheckBox diaCheckBox = (CheckBox) child;
                diaCheckBox.setChecked(false);
            }
        }
        spinCategoria.setSelection(0);
        spinLocalizacion.setSelection(0);
        hora_i.setHour(8);
        hora_i.setMinute(0);
        hora_f.setHour(12);
        hora_f.setMinute(0);

        estadoInicialRB();
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

        // Cargar todas las localidades
        new DataLocalidades(getContext()).obtenerTodasLasLocalidades(new DataLocalidades.OnLocalidadesRetrievedCallback() {
            @Override
            public void onLocalidadesRetrieved(List<Localidad> localidades) {
                listaLocalidades = new ArrayList<>(localidades);

                // Crear el adaptador para el Spinner de localidades
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
    }

    private String obtenerDiasDisponiblesDeArray(ArrayList<EnumDias> diasDisponibles) {
        StringBuilder dias = new StringBuilder();
        for (EnumDias dia : diasDisponibles) {
            String diaFormatted = dia.name();
            dias.append(diaFormatted).append("-");
        }
        if (dias.length() > 0) {
            dias.setLength(dias.length() - 1);
        }
        return dias.toString();
    }

    public void selectedLibreDias(View view){
        if(rb_libre.isChecked()){
            estadoInicialRB();
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
            hora_f.setEnabled(true);
            hora_i.setEnabled(true);
        }

    }

    public void estadoInicialRB(){
        for (int i = 0; i < diasGroup.getChildCount(); i++) {
            View child = diasGroup.getChildAt(i);
            if (child instanceof CheckBox) {
                child.setEnabled(false);
            }
        }
        rb_libre.setEnabled(true);
        rb_libre.setChecked(true);
        rb_libre_horario.setChecked(true);
        rb_libre_horario.setEnabled(true);
        rb_horario.setEnabled(false);
        hora_f.setEnabled(false);
        hora_i.setEnabled(false);
    }

}
