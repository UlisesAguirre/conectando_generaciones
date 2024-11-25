package com.example.tpint_grupo2;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tpint_grupo2.conexion.DataProvincias;
import com.example.tpint_grupo2.conexion.DataUsuarios;
import com.example.tpint_grupo2.entidades.Provincia;
import com.example.tpint_grupo2.conexion.DataLocalidades;
import com.example.tpint_grupo2.entidades.Localidad;
import com.example.tpint_grupo2.enums.EnumNacionalidades;
import com.example.tpint_grupo2.enums.EnumTiposUsuario;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RegistrarseAdultoActivity extends AppCompatActivity {

    private EditText etNombre, etApellido, etDocumento, etFechaNacimiento, etTelefono, etDomicilio, etEmail, etContrasena, etRepetirContrasena, etInstitucion;
    private Spinner spNacionalidad, spSexo, spProvincia, spLocalidad;
    private Button btnConfirmar;

    private List<Localidad> todasLasLocalidades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrarseadulto);

        inicializarVistas();

        cargarTodasLasLocalidades();
        cargarPaises();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etFechaNacimiento.setOnClickListener(view -> {
            final Calendar calendario = Calendar.getInstance();
            int anio = calendario.get(Calendar.YEAR);
            int mes = calendario.get(Calendar.MONTH);
            int dia = calendario.get(Calendar.DAY_OF_MONTH);

            calendario.add(Calendar.YEAR, -60);
            long fechaMinima=calendario.getTimeInMillis();

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    RegistrarseAdultoActivity.this,
                    (datePicker, selectedYear, selectedMonth, selectedDay) -> {
                        String fechaSeleccionada = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                        etFechaNacimiento.setText(fechaSeleccionada);
                    },
                    anio, mes, dia
            );
            datePickerDialog.getDatePicker().setMaxDate(fechaMinima);
            datePickerDialog.show();
        });

        btnConfirmar.setOnClickListener(v -> registrarAdulto());
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
            spProvincia.setAdapter(adapter);

            spProvincia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        spLocalidad.setAdapter(adapter);
    }

    private void cargarPaises() {
        ArrayAdapter<EnumNacionalidades> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, EnumNacionalidades.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spNacionalidad.setAdapter(adapter);
    }

    private void inicializarVistas() {
        etNombre = findViewById(R.id.et_nombre);
        etApellido = findViewById(R.id.et_apellido);
        etDocumento = findViewById(R.id.et_documento);
        spNacionalidad = findViewById(R.id.sp_nacionalidad);
        spSexo = findViewById(R.id.sp_sexo);
        etFechaNacimiento = findViewById(R.id.et_fecha_nacimiento);
        etTelefono = findViewById(R.id.et_telefono);
        spProvincia = findViewById(R.id.sp_provincia);
        spLocalidad = findViewById(R.id.sp_localidad);
        etDomicilio = findViewById(R.id.et_domicilio);
        etEmail = findViewById(R.id.et_email);
        etContrasena = findViewById(R.id.et_contrasena);
        etRepetirContrasena = findViewById(R.id.et_repetir_contrasena);
        etInstitucion = findViewById(R.id.et_institucion);
        btnConfirmar = findViewById(R.id.btn_confirmar);
    }

    private void registrarAdulto() {
        String nombre = etNombre.getText().toString().trim();
        String apellido = etApellido.getText().toString().trim();
        String documento = etDocumento.getText().toString().trim();
        String nacionalidad = spNacionalidad.getSelectedItem().toString();
        String sexo = spSexo.getSelectedItem().toString().substring(0, 1);
        String fechaNacimiento = etFechaNacimiento.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String domicilio = etDomicilio.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String contrasena = etContrasena.getText().toString();
        String repetirContrasena = etRepetirContrasena.getText().toString();
        String institucion = etInstitucion.getText().toString().trim();

        // Validar que todos los campos estén completos
        if (nombre.isEmpty() || apellido.isEmpty() || documento.isEmpty() || fechaNacimiento.isEmpty()
                || telefono.isEmpty() || domicilio.isEmpty() || email.isEmpty()
                || contrasena.isEmpty() || repetirContrasena.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar que nombre y apellido solo contengan letras
        if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            Toast.makeText(this, "El nombre solo debe contener letras.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!apellido.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            Toast.makeText(this, "El apellido solo debe contener letras.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar que documento y teléfono solo contengan números
        if (!documento.matches("\\d+")) {
            Toast.makeText(this, "El documento solo debe contener números.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!telefono.matches("\\d+")) {
            Toast.makeText(this, "El teléfono solo debe contener números.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar formato de email
        if (!email.matches("^[\\w-\\.]+@[\\w-\\.]+\\.[a-zA-Z]{2,}$")) {
            Toast.makeText(this, "El email no tiene un formato válido.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar que las contraseñas coincidan
        if (!contrasena.equals(repetirContrasena)) {
            Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show();
            return;
        }

        Provincia provinciaSeleccionada = (Provincia) spProvincia.getSelectedItem();
        Localidad localidadSeleccionada = (Localidad) spLocalidad.getSelectedItem();
        int idProvincia = provinciaSeleccionada.getIdProvincia();
        int idLocalidad = localidadSeleccionada.getIdLocalidad();

        DataUsuarios dataUsuarios = new DataUsuarios(RegistrarseAdultoActivity.this);
        dataUsuarios.registrarse(nombre, apellido, documento, nacionalidad, sexo, fechaNacimiento, telefono, institucion,
                idLocalidad, domicilio, email, contrasena, EnumTiposUsuario.ADULTO_MAYOR.toString(), success -> {
                    if (success) {
                        Intent intent = new Intent(RegistrarseAdultoActivity.this, InteresesActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Error al registrar el adulto. Por favor, intente nuevamente.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}