package com.example.tpint_grupo2;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tpint_grupo2.conexion.DataActividadCoordinada;
import com.example.tpint_grupo2.conexion.DataActividades;
import com.example.tpint_grupo2.entidades.Actividad;
import com.example.tpint_grupo2.entidades.ActividadCoordinada;
import com.example.tpint_grupo2.entidades.PerfilCompatible;
import com.example.tpint_grupo2.entidades.Usuario;
import com.example.tpint_grupo2.enums.EnumDias;
import com.example.tpint_grupo2.enums.EnumEstadosActividades;
import com.example.tpint_grupo2.enums.EnumTiposUsuario;
import com.example.tpint_grupo2.sessions.UsuariosCompatiblesSession;
import com.example.tpint_grupo2.sessions.UsuariosSession;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CoordinarActividadFragment extends Fragment {

    private Spinner spinnerActividades;
    private TextView txtFecha, txtHora;
    private Button btnVerificarDisponibilidad, btnEnviarSolicitud;
    private TextView txtNombreUsuario, txtEmailUsuario, txtSexo, txtActividadesPreferidas, txt_dias_disponibles, txt_horarios_disponibles;
    private ImageView imgUsuario;
    private int idActividad;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coordinar_actividad, container, false);

        // Inicializar los elementos del layout
        spinnerActividades = view.findViewById(R.id.spinner_actividades);
        txtFecha = view.findViewById(R.id.txt_fecha);
        txtHora = view.findViewById(R.id.txt_hora);
        txtNombreUsuario = view.findViewById(R.id.txt_nombre_usuario);
        txtEmailUsuario = view.findViewById(R.id.txt_email_usuario);
        txtSexo = view.findViewById(R.id.txt_sexo);
        txtActividadesPreferidas = view.findViewById(R.id.txt_actividades_preferidas);
        txt_dias_disponibles = view.findViewById(R.id.txt_dias_disponibles);
        txt_horarios_disponibles = view.findViewById(R.id.txt_horarios_disponibles);
        btnVerificarDisponibilidad = view.findViewById(R.id.btn_verificar_disponibilidad);
        btnEnviarSolicitud = view.findViewById(R.id.btn_enviar_solicitud);
        imgUsuario = view.findViewById(R.id.img_usuario);

        // Obtener las actividades desde la base de datos
        UsuariosCompatiblesSession session = new UsuariosCompatiblesSession(getContext());
        PerfilCompatible perfil = session.getPerfilCompatible();
        int documentoSeleccionado = perfil.getNro_documento();

        DataActividades dataActividades = new DataActividades(getContext());

        // Campo de clase para almacenar las actividades
        ArrayList<Actividad> actividades = new ArrayList<>();

        dataActividades.obtenerActividadesPorUsuario(actividadesObtenidas -> {
            actividades.clear(); // Limpiar la lista por si se reutiliza
            actividades.addAll(actividadesObtenidas);

            ArrayList<String> titulosActividades = new ArrayList<>();
            titulosActividades.add("Seleccione una actividad");
            for (Actividad actividad : actividades) {
                String tituloConDias = actividad.getTitulo() + " (" + actividad.getDiasDictadosComoString() + ")";
                titulosActividades.add(tituloConDias);
            }

            // Configurar el adaptador para el Spinner
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, titulosActividades);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerActividades.setAdapter(adapter);
        }, documentoSeleccionado);

        // Configurar el listener del Spinner
        spinnerActividades.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { // Asegurarse de que no sea la opción por defecto
                    Actividad actividadSeleccionada = actividades.get(position - 1); // Restar 1 por "Seleccione una actividad"
                    idActividad = actividadSeleccionada.getIdActividad(); // Obtener el id de la actividad seleccionada
                    Toast.makeText(getContext(), "Seleccionaste: " + actividadSeleccionada.getTitulo(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nada seleccionado
            }
        });

        cargarDatosUsuario();
        txtFecha.setOnClickListener(v -> mostrarDatePicker());
        txtHora.setOnClickListener(v -> mostrarTimePicker());
        btnVerificarDisponibilidad.setOnClickListener(v -> verificarDisponibilidad());
        btnEnviarSolicitud.setOnClickListener(v -> enviarSolicitud());

        return view;
    }


    @SuppressLint("SetTextI18n")
    private void cargarDatosUsuario() {
        UsuariosCompatiblesSession session = new UsuariosCompatiblesSession(getContext());
        PerfilCompatible perfil = session.getPerfilCompatible();

        if (perfil != null) {
            // Mostrar el nombre completo
            txtNombreUsuario.setText("Nombre: " + perfil.getNombre() + " " + perfil.getApellido());

            // Mostrar el correo electrónico
            txtEmailUsuario.setText("Email: " + perfil.getEmail());

            // Convertir el sexo de abreviado a completo
            String sexoCompleto;
            if ("M".equals(perfil.getSexo())) {
                sexoCompleto = "Sexo: Masculino";
            } else if ("F".equals(perfil.getSexo())) {
                sexoCompleto = "Sexo: Femenino";
            } else {
                sexoCompleto = perfil.getSexo(); // Manejo de cualquier otro valor
            }
            txtSexo.setText(sexoCompleto);

            // Mostrar actividades preferidas
            txtActividadesPreferidas.setText("Actividades Preferidas: " + perfil.getTituloActividad());

            // Mostrar Días Disponibles
            txt_dias_disponibles.setText("Días Disponibles: " + perfil.getDiasDisponibles());

            // Mostrar Horarios Disponibles
            txt_horarios_disponibles.setText("Horarios Disponibles: " + perfil.getHorariosDisponibles());

        } else {
            Toast.makeText(getContext(), "No se encontró un perfil compatible guardado.", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int anio = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);

        // Establecer el límite para el DatePicker: no permitir fechas anteriores a hoy
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (DatePicker view, int year, int month, int dayOfMonth) -> {
            String fechaSeleccionada = dayOfMonth + "/" + (month + 1) + "/" + year;
            txtFecha.setText(fechaSeleccionada);
        }, anio, mes, dia);

        // No permitir fechas anteriores a la fecha actual
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

        datePickerDialog.show();
    }

    private void mostrarTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int horaActual = calendar.get(Calendar.HOUR_OF_DAY);
        int minutoActual = calendar.get(Calendar.MINUTE);

        // Obtener la fecha seleccionada en el DatePicker (suponiendo que ya se tiene la fecha en txtFecha)
        String fechaSeleccionada = txtFecha.getText().toString(); // Formato esperado: "dd/MM/yyyy"

        // Parsear la fecha seleccionada para obtener el día, mes y año
        String[] partesFecha = fechaSeleccionada.split("/");

        try{
            int diaSeleccionado = Integer.parseInt(partesFecha[0]);
            int mesSeleccionado = Integer.parseInt(partesFecha[1]) - 1; // Meses en Calendar empiezan en 0
            int anioSeleccionado = Integer.parseInt(partesFecha[2]);

            // Crear un objeto Calendar con la fecha seleccionada
            Calendar fechaSeleccionadaCalendar = Calendar.getInstance();
            fechaSeleccionadaCalendar.set(anioSeleccionado, mesSeleccionado, diaSeleccionado);

            // Verificar si el día seleccionado es hoy
            boolean esDiaActual = fechaSeleccionadaCalendar.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR) &&
                    fechaSeleccionadaCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR);

            // Crear el TimePickerDialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (TimePicker view, int hourOfDay, int minute) -> {
                // Si el día seleccionado es el día actual, verificamos las restricciones
                if (esDiaActual) {
                    // Calcular la diferencia de horas entre la hora seleccionada y la hora actual
                    Calendar horaSeleccionadaCalendar = Calendar.getInstance();
                    horaSeleccionadaCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    horaSeleccionadaCalendar.set(Calendar.MINUTE, minute);

                    // Diferencia en milisegundos
                    long diferenciaMilisegundos = horaSeleccionadaCalendar.getTimeInMillis() - calendar.getTimeInMillis();
                    long diferenciaHoras = diferenciaMilisegundos / (1000 * 60 * 60); // Convertir milisegundos a horas

                    // Verificar si la hora seleccionada es anterior a la actual
                    if (horaSeleccionadaCalendar.before(calendar)) {
                        // Si la hora seleccionada es anterior, mostramos un mensaje de error
                        Toast.makeText(getContext(), "No se puede seleccionar una hora anterior al horario actual", Toast.LENGTH_SHORT).show();
                        txtHora.setText("Seleccione la Hora"); // Restablecer el texto al valor predeterminado
                    }
                    // Verificar si la diferencia es menor a 2 horas
                    else if (diferenciaHoras < 2) {
                        // Si la diferencia es menor a 2 horas, mostramos un mensaje de error
                        Toast.makeText(getContext(), "Las actividades requieren mínimo 2 horas de anticipación", Toast.LENGTH_SHORT).show();
                        txtHora.setText("Seleccione la Hora"); // Restablecer el texto al valor predeterminado
                    } else {
                        // Si la hora es válida (al menos 2 horas después y no anterior al actual)
                        String horaSeleccionada = String.format("%02d:%02d", hourOfDay, minute);
                        txtHora.setText(horaSeleccionada);
                    }
                } else {
                    // Si el día seleccionado es posterior, no hay restricción sobre la hora
                    String horaSeleccionada = String.format("%02d:%02d", hourOfDay, minute);
                    txtHora.setText(horaSeleccionada);
                }
            }, horaActual, minutoActual, true);

            // Mostrar el TimePicker
            timePickerDialog.show();
        }catch (Exception e){
            Toast.makeText(getContext(),"Seleccione primero una fecha",Toast.LENGTH_SHORT).show();
        }

    }


    private void verificarDisponibilidad() {
        // Obtener los valores de fecha y hora seleccionados
        String fechaSeleccionada = txtFecha.getText().toString(); // Formato esperado: "dd/MM/yyyy"
        String horaSeleccionada = txtHora.getText().toString(); // Formato esperado: "HH:mm"

        // Verificar que la fecha no sea la predeterminada "Selecciona la Fecha"
        if (fechaSeleccionada.equals("Selecciona la Fecha")) {
            Toast.makeText(getContext(), "Por favor, selecciona una fecha.", Toast.LENGTH_SHORT).show();
            return;  // Salir del método si la fecha no está seleccionada
        }

        // Verificar que la hora no sea la predeterminada "Selecciona la Hora"
        if (horaSeleccionada.equals("Selecciona la Hora")) {
            Toast.makeText(getContext(), "Por favor, selecciona una hora.", Toast.LENGTH_SHORT).show();
            return;  // Salir del método si la hora no está seleccionada
        }

        // Si ambas fecha y hora están seleccionadas, proceder con la lógica de verificación de disponibilidad
        try {
            UsuariosCompatiblesSession session = new UsuariosCompatiblesSession(getContext());
            PerfilCompatible perfil = session.getPerfilCompatible();

            // Verifica si el perfil es null
            if (perfil == null) {
                Toast.makeText(getContext(), "No se encontró un perfil compatible guardado.", Toast.LENGTH_SHORT).show();
                return; // Termina el método si no hay perfil
            }

            // Obtener los días y horarios disponibles del perfil
            String diasDisponibles = perfil.getDiasDisponibles();
            String horariosDisponibles = perfil.getHorariosDisponibles(); // Ejemplo: "09:00 - 12:00,14:00 - 18:00"

            // Verificar si los valores son null o vacíos
            if (diasDisponibles == null || diasDisponibles.isEmpty()) {
                Log.e("CoordinarActividad", "Días disponibles no encontrados.");
                Toast.makeText(getContext(), "No hay días disponibles para el perfil.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (horariosDisponibles == null || horariosDisponibles.isEmpty()) {
                Log.e("CoordinarActividad", "Horarios disponibles no encontrados.");
                Toast.makeText(getContext(), "No hay horarios disponibles para el perfil.", Toast.LENGTH_SHORT).show();
                return;
            }



            // Dividir los días y horarios en arrays
            String[] diasDisponiblesArray = diasDisponibles.split("-");
            String[] horariosDisponiblesArray = horariosDisponibles.split(",");

            // Obtener el nombre del día de la fecha seleccionada
            Calendar calendar = Calendar.getInstance();
            String[] partesFecha = fechaSeleccionada.split("/");
            int dia = Integer.parseInt(partesFecha[0]);
            int mes = Integer.parseInt(partesFecha[1]) - 1; // Los meses en Calendar empiezan en 0
            int anio = Integer.parseInt(partesFecha[2]);
            calendar.set(anio, mes, dia);
            String diaDeLaSemana = obtenerNombreDia(calendar.get(Calendar.DAY_OF_WEEK));

            // Obtener la actividad seleccionada
            String actividadSeleccionada = (String) spinnerActividades.getSelectedItem();

            // Verificar que se haya seleccionado una actividad
            if (actividadSeleccionada == null || actividadSeleccionada.equals("Seleccione una actividad")) {
                Toast.makeText(getContext(), "Por favor, selecciona una actividad.", Toast.LENGTH_SHORT).show();
                return; // Salir del método si no se seleccionó actividad
            }

            // Extraer los días dictados de la actividad seleccionada
            // Asumimos que los días están entre paréntesis en el formato: "Actividad (Lunes, Miércoles, Viernes)"
            String diasActividadString = actividadSeleccionada.substring(actividadSeleccionada.indexOf("(") + 1, actividadSeleccionada.indexOf(")"));
            String[] diasActividadArray = diasActividadString.split("-"); // Ahora tenemos los días en un array

            // Comprobar si el día seleccionado coincide con los días disponibles del perfil
            boolean diaDisponible = false;
            boolean libre=false;
            if(diasActividadString.equals(EnumDias.LIBRE.toString())){
                libre=true;
            }
            for (String diaDisponibleStr : diasDisponiblesArray) {
                if (diaDeLaSemana.equalsIgnoreCase(diaDisponibleStr)) {
                    diaDisponible = true;
                    break;
                }
            }

            // Comprobar si el día seleccionado coincide con los días dictados por la actividad
            boolean diaActividadValido = false;
            for (String diaActividadStr : diasActividadArray) {
                if (diaDeLaSemana.equalsIgnoreCase(diaActividadStr.trim()) && diaDisponible) {
                    diaActividadValido = true;
                    break;
                }
            }

            // Si el día es válido, verificar la hora seleccionada
            boolean horaDisponible = false;

            if (diaActividadValido||libre) {
                for (String rangoHorario : horariosDisponiblesArray) {
                    String[] horas = rangoHorario.trim().split(" - ");
                    if (horas.length == 2) {
                        String horaInicio = horas[0].trim();
                        String horaFin = horas[1].trim();

                        // Comparar la hora seleccionada con el rango horario
                        if (horaSeleccionada.compareTo(horaInicio) >= 0 && horaSeleccionada.compareTo(horaFin) <= 0) {
                            horaDisponible = true;
                            break;
                        }
                    }
                }
            }

            // Mostrar el resultado al usuario
            if ((diaActividadValido && horaDisponible)||(libre && diaDisponible && horaDisponible)) {
                Toast.makeText(getContext(), "Es posible coordinar en la fecha y hora seleccionadas.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "No es posible coordinar en la fecha y hora seleccionadas.", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e("CoordinarActividad", "Error al verificar disponibilidad: " + e.getMessage());
            Toast.makeText(getContext(), "Ocurrió un error al verificar la disponibilidad.", Toast.LENGTH_SHORT).show();
        }
    }



    private String obtenerNombreDia(int numeroDia) {
        // Los días de la semana en Calendar empiezan desde 1 (domingo) hasta 7 (sábado)
        switch (numeroDia) {
            case Calendar.SUNDAY:
                return "Domingo";
            case Calendar.MONDAY:
                return "Lunes";
            case Calendar.TUESDAY:
                return "Martes";
            case Calendar.WEDNESDAY:
                return "Miercoles";
            case Calendar.THURSDAY:
                return "Jueves";
            case Calendar.FRIDAY:
                return "Viernes";
            case Calendar.SATURDAY:
                return "Sabado";
            default:
                return ""; // Retorna vacío si no es un día válido
        }
    }

    private void enviarSolicitud() {
        // Obtener los valores de fecha, hora y actividad seleccionados
        String fechaSeleccionada = txtFecha.getText().toString();
        String horaSeleccionada = txtHora.getText().toString();
        String actividadSeleccionada = (String) spinnerActividades.getSelectedItem();

        // Verificar que se haya seleccionado una actividad
        if (actividadSeleccionada == null || actividadSeleccionada.equals("Seleccione una actividad")) {
            Toast.makeText(getContext(), "Por favor, selecciona una actividad.", Toast.LENGTH_SHORT).show();
            return; // Salir del método si no se seleccionó actividad
        }

        // Verificar que se haya seleccionado una fecha
        if (fechaSeleccionada.equals("Selecciona la Fecha")) {
            Toast.makeText(getContext(), "Por favor, selecciona una fecha.", Toast.LENGTH_SHORT).show();
            return; // Salir del método si no se seleccionó fecha
        }

        // Verificar que se haya seleccionado una hora
        if (horaSeleccionada.equals("Selecciona la Hora")) {
            Toast.makeText(getContext(), "Por favor, selecciona una hora.", Toast.LENGTH_SHORT).show();
            return; // Salir del método si no se seleccionó hora
        }

        // Verificar la disponibilidad del usuario
        try {
            UsuariosCompatiblesSession session = new UsuariosCompatiblesSession(getContext());
            PerfilCompatible perfil = session.getPerfilCompatible();

            if (perfil == null) {
                Toast.makeText(getContext(), "No se encontró un perfil compatible guardado.", Toast.LENGTH_SHORT).show();
                return; // Salir del método si no se encontró perfil
            }

            // Verificar disponibilidad del usuario
            String diasDisponibles = perfil.getDiasDisponibles();
            String horariosDisponibles = perfil.getHorariosDisponibles(); // Ejemplo: "09:00 - 12:00,14:00 - 18:00"

            // Verificar que los días y horarios disponibles no sean vacíos
            if (diasDisponibles == null || diasDisponibles.isEmpty()) {
                Toast.makeText(getContext(), "No hay días disponibles para el perfil.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (horariosDisponibles == null || horariosDisponibles.isEmpty()) {
                Toast.makeText(getContext(), "No hay horarios disponibles para el perfil.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Dividir los días y horarios en arrays
            String[] diasDisponiblesArray = diasDisponibles.split("-");
            String[] horariosDisponiblesArray = horariosDisponibles.split(",");

            // Obtener el nombre del día de la fecha seleccionada
            Calendar calendar = Calendar.getInstance();
            String[] partesFecha = fechaSeleccionada.split("/");
            int dia = Integer.parseInt(partesFecha[0]);
            int mes = Integer.parseInt(partesFecha[1]) - 1; // Los meses en Calendar empiezan en 0
            int anio = Integer.parseInt(partesFecha[2]);
            calendar.set(anio, mes, dia);
            String diaDeLaSemana = obtenerNombreDia(calendar.get(Calendar.DAY_OF_WEEK));

            // Comprobar si el día seleccionado está disponible
            boolean diaDisponible = false;
            for (String diaDisponibleStr : diasDisponiblesArray) {
                if (diaDeLaSemana.equalsIgnoreCase(diaDisponibleStr)) {
                    diaDisponible = true;
                    break;
                }
            }

            // Comprobar si la hora seleccionada está dentro de los horarios disponibles
            boolean horaDisponible = false;
            for (String rangoHorario : horariosDisponiblesArray) {
                String[] horas = rangoHorario.trim().split(" - ");
                if (horas.length == 2) {
                    String horaInicio = horas[0].trim();
                    String horaFin = horas[1].trim();

                    // Comparar la hora seleccionada con el rango horario
                    if (horaSeleccionada.compareTo(horaInicio) >= 0 && horaSeleccionada.compareTo(horaFin) <= 0) {
                        horaDisponible = true;
                        break;
                    }
                }
            }

            // Verificar disponibilidad
            if (diaDisponible && horaDisponible) {
                Toast.makeText(getContext(), "Solicitud para la actividad: " + actividadSeleccionada, Toast.LENGTH_SHORT).show();

                // Convertir la fecha seleccionada al formato yyyy-MM-dd
                SimpleDateFormat sdfInput = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                SimpleDateFormat sdfOutput = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = sdfInput.parse(fechaSeleccionada);
                String fechaSeleccionadaConvertida = sdfOutput.format(date);

                // Obtener usuario seleccionado
                UsuariosCompatiblesSession sessionPC = new UsuariosCompatiblesSession(getContext());
                PerfilCompatible perfilCompatible = sessionPC.getPerfilCompatible();

                // Obtener usuario logueado
                UsuariosSession userSession = new UsuariosSession(getContext());
                Usuario usuario = userSession.getUser();

                // Crear el objeto ActividadCoordinada
                ActividadCoordinada actividadCoordinada = new ActividadCoordinada();
                actividadCoordinada.setIdActividadBD(idActividad);

                if (usuario.getTipo_usuario() == EnumTiposUsuario.VOLUNTARIO) {
                    actividadCoordinada.setDniVoluntarioBD(usuario.getNro_documento());
                    actividadCoordinada.setDniAdultoMayorBD(perfilCompatible.getNro_documento());
                    actividadCoordinada.setEstadoVoluntarioBD(String.valueOf(EnumEstadosActividades.ENVIADO));
                    actividadCoordinada.setEstadoAdultoBD(String.valueOf(EnumEstadosActividades.PENDIENTE));
                } else {
                    actividadCoordinada.setDniVoluntarioBD(perfilCompatible.getNro_documento());
                    actividadCoordinada.setDniAdultoMayorBD(usuario.getNro_documento());
                    actividadCoordinada.setEstadoVoluntarioBD(String.valueOf(EnumEstadosActividades.PENDIENTE));
                    actividadCoordinada.setEstadoAdultoBD(String.valueOf(EnumEstadosActividades.ENVIADO));
                }

                actividadCoordinada.setFechaParticipar(fechaSeleccionadaConvertida);
                actividadCoordinada.setFechaSolicitud(fechaActual()); // Fecha actual
                actividadCoordinada.setHorario(horaSeleccionada);

                // Crear la instancia de DataActividadCoordinada
                DataActividadCoordinada dataActividadCoordinada = new DataActividadCoordinada(getContext());

                // Verificar si la actividad ya existe antes de insertarla
                dataActividadCoordinada.insertarActividadCoordinada(actividadCoordinada);
            } else {
                Toast.makeText(getContext(), "El usuario no está disponible en la fecha y hora seleccionadas.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("CoordinarActividad", "Error al verificar disponibilidad: " + e.getMessage());
            Toast.makeText(getContext(), "Ocurrió un error al verificar la disponibilidad.", Toast.LENGTH_SHORT).show();
        }
    }

    private String fechaActual() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }


}
