package com.example.tpint_grupo2;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tpint_grupo2.adapter.ActividadesAdapter;
import com.example.tpint_grupo2.adapter.CategoriasAdapter;
import com.example.tpint_grupo2.conexion.DataActividades;
import com.example.tpint_grupo2.conexion.DataIntereses;
import com.example.tpint_grupo2.conexion.DataLocalidades;
import com.example.tpint_grupo2.conexion.DataProvincias;
import com.example.tpint_grupo2.conexion.DataCategorias;
import com.example.tpint_grupo2.entidades.Actividad;
import com.example.tpint_grupo2.entidades.Categoria;
import com.example.tpint_grupo2.entidades.Intereses;
import com.example.tpint_grupo2.entidades.Localidad;
import com.example.tpint_grupo2.entidades.Provincia;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.example.tpint_grupo2.entidades.Usuario;
import com.example.tpint_grupo2.enums.EnumDias;
import com.example.tpint_grupo2.sessions.UsuariosSession;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

public class InteresesActivity extends AppCompatActivity {

    private Spinner spinnerProvincia, spinnerLocalidad;
    private List<Localidad> todasLasLocalidades;
    private LinearLayout zonaSeleccionadaContainer;
    private Button btnAgregarZona;

    private Button buttonDesde, buttonHasta;

    private RecyclerView recyclerCategorias;
    private List<Categoria> categoriasList; // Lista de actividades
    private CategoriasAdapter categoriasAdapter;

    private Button buttonConfirmar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intereses);

        inicializarVistas();
        cargarTodasLasLocalidades();

        recyclerCategorias = findViewById(R.id.recycler_actividades);
        recyclerCategorias.setLayoutManager(new GridLayoutManager(this, 2));

        cargarCategorias();


        btnAgregarZona = findViewById(R.id.btn_agregar_zona);
        btnAgregarZona.setOnClickListener(v -> agregarZonaSeleccionada());

        buttonDesde = findViewById(R.id.button_timepicker_desde);
        buttonHasta = findViewById(R.id.button_timepicker_hasta);

        buttonConfirmar = findViewById(R.id.btn_confirmar);

        buttonDesde.setOnClickListener(view -> {
            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(12)
                    .setMinute(0)
                    .build();

            timePicker.addOnPositiveButtonClickListener(v -> {
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();

                buttonDesde.setText(String.format("%02d:%02d", hour, minute));
            });

            timePicker.show(getSupportFragmentManager(), "tag");
        });

        buttonHasta.setOnClickListener(view -> {
            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(18)
                    .setMinute(0)
                    .build();

            timePicker.addOnPositiveButtonClickListener(v -> {
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();

                buttonHasta.setText(String.format("%02d:%02d", hour, minute));
            });

            timePicker.show(getSupportFragmentManager(), "tag");
        });

        cargarInteresesUsuario();

        buttonConfirmar.setOnClickListener(v -> {
            UsuariosSession userSession = new UsuariosSession(this);
            Usuario usuario = userSession.getUser();

            // 1. Recopila días seleccionados en formato 'dia-dia'
            GridLayout diasGroup = findViewById(R.id.dias_disponibles_group);
            ArrayList<EnumDias> diasSeleccionados = new ArrayList<>();

            for (int i = 0; i < diasGroup.getChildCount(); i++) {
                View child = diasGroup.getChildAt(i);
                if (child instanceof CheckBox) {
                    CheckBox diaCheckBox = (CheckBox) child;
                    if (diaCheckBox.isChecked()) {
                        String diaTexto = diaCheckBox.getText().toString();
                        EnumDias diaEnum = EnumDias.valueOf(diaTexto);
                        diasSeleccionados.add(diaEnum);
                    }
                }
            }

            // 2. Obtener el rango horario
            String horaDesde = buttonDesde.getText().toString();
            String horaHasta = buttonHasta.getText().toString();
            String rangoHorario = horaDesde + " - " + horaHasta;


            // 3. Obtener el valor de transporte (booleano)
            RadioGroup transporteGroup = findViewById(R.id.transporte_group);
            boolean transporte = transporteGroup.getCheckedRadioButtonId() == R.id.radio_si;

            // 4. Recopila las IDs de las localidades seleccionadas
            StringBuilder localidadesSeleccionadas = new StringBuilder();
            for (int i = 0; i < zonaSeleccionadaContainer.getChildCount(); i++) {
                View child = zonaSeleccionadaContainer.getChildAt(i);
                if (child instanceof LinearLayout) {
                    LinearLayout itemContainer = (LinearLayout) child;
                    String tag_localidad=itemContainer.getTag().toString();
                    for (int j = 0; j < itemContainer.getChildCount(); j++) {
                        View innerChild = itemContainer.getChildAt(j);
                        if (innerChild instanceof TextView) {
                            TextView localidadTextView = (TextView) innerChild;
                            if (localidadesSeleccionadas.length() > 0) {
                                localidadesSeleccionadas.append(",");
                            }
                            if(tag_localidad!=null){
                                localidadesSeleccionadas.append(tag_localidad);
                            }else{
                                System.out.println("No hay tag");
                            }
                            break;
                        }
                    }
                }
            }

            // 5. Recopila las IDs de las actividades seleccionadas
            String categoriasSeleccionadas = categoriasAdapter.getCategoriasSeleccionadas();

            if(!(horaDesde.equals("Seleccionar")||horaHasta.equals("Seleccionar")
                    ||diasSeleccionados.isEmpty()||categoriasSeleccionadas.isEmpty()||localidadesSeleccionadas.toString().isEmpty())){
                Intereses intereses = new Intereses();
                intereses.setUsuario(usuario);
                intereses.setDiasDisponibles(diasSeleccionados);
                intereses.setHorario(rangoHorario);
                intereses.setTransporte(transporte);

                ArrayList<Localidad> localidades = new ArrayList<>();
                for (String localidadId : localidadesSeleccionadas.toString().split(",")) {
                    Localidad localidad = new Localidad();
                    localidad.setIdLocalidad(Integer.parseInt(localidadId));
                    localidades.add(localidad);
                }
                intereses.setLocalidades(localidades);

                ArrayList<Categoria> categorias = new ArrayList<>();
                for (String categoriaId : categoriasSeleccionadas.split(",")) {
                    Categoria categoria = new Categoria();
                    categoria.setIdCategoria(Integer.parseInt(categoriaId));
                    categorias.add(categoria);
                }
                intereses.setCategorias(categorias);

                boolean isFromModifyButton = getIntent().getBooleanExtra("isFromModifyButton", false);
                DataIntereses dataIntereses = new DataIntereses(this);
                if (isFromModifyButton) {
                    int nroDocumento = userSession.getUser().getNro_documento();

                    dataIntereses.borrarIntereses(nroDocumento, exito -> {
                        if (exito) {
                            Log.d("BorrarIntereses", "Intereses borrados exitosamente.");
                            dataIntereses.registrarIntereses(intereses, exitoInsert -> {
                                if (exitoInsert) {
                                    Toast.makeText(this, "Intereses actualizados exitosamente.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(this, InicioActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(this, "Error al registrar intereses", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Log.e("BorrarIntereses", "Error al borrar intereses.");
                            Toast.makeText(this, "Error al borrar los intereses anteriores", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    dataIntereses.registrarIntereses(intereses, exito -> {
                        if (exito) {
                            Toast.makeText(this, "Intereses registrados exitosamente.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, InicioActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "Error al registrar intereses", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else if (diasSeleccionados.isEmpty()) {
                Toast.makeText(this, "Debe seleccionar al menos un dia", Toast.LENGTH_SHORT).show();
            } else if (categoriasSeleccionadas.isEmpty()) {
                Toast.makeText(this, "Debe seleccionar al menos una actividad", Toast.LENGTH_SHORT).show();
            } else if (localidadesSeleccionadas.toString().isEmpty()) {
                Toast.makeText(this, "Debe agregar al menos una localidad", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Debe seleccionar un horario valido", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarCategorias() {
        DataCategorias dataCategorias = new DataCategorias(this);
        dataCategorias.obtenerCategorias(categorias -> {
            categoriasList = categorias;
            categoriasAdapter = new CategoriasAdapter(categoriasList);  // Adaptador para las categorías
            recyclerCategorias.setAdapter(categoriasAdapter);  // Asegúrate de tener el RecyclerView adecuado
        });
    }

    private void inicializarVistas() {
        spinnerProvincia = findViewById(R.id.spinner_provincia);
        spinnerLocalidad = findViewById(R.id.spinner_localidad);
        zonaSeleccionadaContainer = findViewById(R.id.zona_seleccionada_container);
    }

    private void agregarZonaSeleccionada() {
        Provincia provinciaSeleccionada = (Provincia) spinnerProvincia.getSelectedItem();
        Localidad localidadSeleccionada = (Localidad) spinnerLocalidad.getSelectedItem();

        if (provinciaSeleccionada != null && localidadSeleccionada != null) {
            for (int i = 0; i < zonaSeleccionadaContainer.getChildCount(); i++) {
                View child = zonaSeleccionadaContainer.getChildAt(i);
                if (child.getTag() != null && child.getTag().equals(localidadSeleccionada.getIdLocalidad())) {
                    Toast.makeText(this, "La zona ya ha sido seleccionada.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            LinearLayout itemContainer = new LinearLayout(this);
            itemContainer.setOrientation(LinearLayout.HORIZONTAL);
            itemContainer.setGravity(Gravity.CENTER_HORIZONTAL);
            itemContainer.setPadding(8, 8, 8, 8);
            itemContainer.setTag(localidadSeleccionada.getIdLocalidad()); // Usar el ID como tag

            TextView textView = new TextView(this);
            textView.setText(provinciaSeleccionada.getNombre() + " - " + localidadSeleccionada.getNombre());
            textView.setTextColor(Color.BLACK);
            textView.setPadding(8, 8, 8, 8);

            Button eliminarButton = new Button(this);
            eliminarButton.setText("-");
            eliminarButton.setTextColor(Color.RED);
            eliminarButton.setTextSize(18);
            eliminarButton.setTypeface(null, Typeface.BOLD);
            eliminarButton.setBackgroundColor(Color.TRANSPARENT);
            eliminarButton.setPadding(16, 16, 16, 16);

            eliminarButton.setOnClickListener(v -> {
                zonaSeleccionadaContainer.removeView(itemContainer);
            });

            itemContainer.addView(textView);
            itemContainer.addView(eliminarButton);

            zonaSeleccionadaContainer.addView(itemContainer);
        }
    }

    private void cargarTodasLasLocalidades() {
        DataLocalidades dataLocalidades = new DataLocalidades(this);
        dataLocalidades.obtenerTodasLasLocalidades(localidades -> {
            todasLasLocalidades = localidades;
            cargarProvincias();
        });
    }

    private void cargarProvincias() {
        DataProvincias dataProvincias = new DataProvincias(this);
        dataProvincias.obtenerProvincias(provincias -> {
            ArrayAdapter<Provincia> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, provincias);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerProvincia.setAdapter(adapter);

            spinnerProvincia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Provincia provinciaSeleccionada = (Provincia) parent.getSelectedItem();
                    cargarLocalidadesPorProvincia(provinciaSeleccionada.getIdProvincia());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        });
    }

    private void cargarLocalidadesPorProvincia(int idProvincia) {
        List<Localidad> localidadesFiltradas = new ArrayList<>();
        for (Localidad localidad : todasLasLocalidades) {
            if (localidad.getProvincia().getIdProvincia() == idProvincia) {
                localidadesFiltradas.add(localidad);
            }
        }

        ArrayAdapter<Localidad> adapter = new ArrayAdapter<Localidad>(this, android.R.layout.simple_spinner_item, localidadesFiltradas) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setText(localidadesFiltradas.get(position).getNombre());
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setText(localidadesFiltradas.get(position).getNombre());
                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocalidad.setAdapter(adapter);
    }

    private void cargarInteresesUsuario() {
        UsuariosSession userSession = new UsuariosSession(this);
        Usuario usuario = userSession.getUser();
        DataIntereses dataIntereses = new DataIntereses(this);

        dataIntereses.obtenerInteresesPorDni(usuario.getNro_documento(), intereses -> {
            if (intereses != null) {
                GridLayout diasGroup = findViewById(R.id.dias_disponibles_group);
                for (int i = 0; i < diasGroup.getChildCount(); i++) {
                    View child = diasGroup.getChildAt(i);
                    if (child instanceof CheckBox) {
                        CheckBox diaCheckBox = (CheckBox) child;
                        EnumDias diaEnum = EnumDias.valueOf(diaCheckBox.getText().toString());
                        if (intereses.getDiasDisponibles().contains(diaEnum)) {
                            diaCheckBox.setChecked(true);
                        }
                    }
                }

                String[] horario = intereses.getHorario().split(" - ");
                buttonDesde.setText(horario[0]);
                buttonHasta.setText(horario[1]);

                RadioGroup transporteGroup = findViewById(R.id.transporte_group);
                int transporteId = intereses.isTransporte() ? R.id.radio_si : R.id.radio_no;
                transporteGroup.check(transporteId);

                for (Localidad localidad : intereses.getLocalidades()) {
                    LinearLayout itemContainer = new LinearLayout(this);
                    itemContainer.setOrientation(LinearLayout.HORIZONTAL);
                    itemContainer.setGravity(Gravity.CENTER_HORIZONTAL);
                    itemContainer.setPadding(8, 8, 8, 8);
                    itemContainer.setTag(localidad.getIdLocalidad());

                    TextView textView = new TextView(this);
                    textView.setText(localidad.getProvincia().getNombre() + " - " + localidad.getNombre());
                    textView.setTextColor(Color.BLACK);
                    textView.setPadding(8, 8, 8, 8);
                    textView.setTag(localidad.getIdLocalidad());

                    Button eliminarButton = new Button(this);
                    eliminarButton.setText("-");
                    eliminarButton.setTextColor(Color.RED);
                    eliminarButton.setTextSize(18);
                    eliminarButton.setTypeface(null, Typeface.BOLD);
                    eliminarButton.setBackgroundColor(Color.TRANSPARENT);
                    eliminarButton.setPadding(16, 16, 16, 16);

                    eliminarButton.setOnClickListener(v -> {
                        zonaSeleccionadaContainer.removeView(itemContainer);
                    });

                    itemContainer.addView(textView);
                    itemContainer.addView(eliminarButton);

                    zonaSeleccionadaContainer.addView(itemContainer);
                }

                categoriasAdapter.setCategoriasSeleccionadas(intereses.getCategorias());
            }
        });
    }
}
