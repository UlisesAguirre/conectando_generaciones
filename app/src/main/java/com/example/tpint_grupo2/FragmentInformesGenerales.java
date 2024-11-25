package com.example.tpint_grupo2;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.tpint_grupo2.conexion.DataCategoriasActividades;
import com.example.tpint_grupo2.conexion.DataInformes;
import com.example.tpint_grupo2.elementos.cargandoDialog;
import com.example.tpint_grupo2.entidades.Categoria;
import com.example.tpint_grupo2.entidades.Usuario;

import java.sql.Date;


public class FragmentInformesGenerales extends Fragment {

    private Spinner spCategorias;
    private TextView txt_cantidad_inscripciones;
    private TextView txt_valor_promedio;
    private TextView txt_actividad_filtrada;
    private TextView txt_detalles_informes;
    private EditText et_fecha_inicio;
    private EditText et_fecha_fin;
    private Button btnAplicarFiltros;
    private ImageButton btnBorrarFiltros;
    private DataInformes dataInformes;
    private RatingBar ratingBar_promedio_calificacion;

    public FragmentInformesGenerales() {

    }

        @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_informes_generales, container, false);
        spCategorias=view.findViewById(R.id.sp_cat_informe_gen);

        String[] cargando={"Cargando..."};
        ArrayAdapter<String> adapterCargando = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, cargando);
        adapterCargando.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategorias.setAdapter(adapterCargando);

        DataCategoriasActividades dataCategoriasActividades=new DataCategoriasActividades();
        dataCategoriasActividades.fetchData(spCategorias);

        //Enlace de controles
        et_fecha_inicio=view.findViewById(R.id.et_fecha_inicio_gen);
        et_fecha_fin=view.findViewById(R.id.et_fecha_fin_gen);
        btnAplicarFiltros=view.findViewById(R.id.btn_aplicar_filtros_gen);
        btnBorrarFiltros=view.findViewById(R.id.btn_borrarFiltros_gen);
        txt_cantidad_inscripciones=view.findViewById(R.id.txt_value_cant_inscrip_gen);
        txt_valor_promedio=view.findViewById(R.id.txt_value_promedio_gen);
        txt_actividad_filtrada=view.findViewById(R.id.txt_actividad_filtrada_gen);
        txt_detalles_informes=view.findViewById(R.id.txt_detalles_filtros_gen);
        ratingBar_promedio_calificacion=view.findViewById(R.id.ratingBar_promedio);

        //Seteo los TXT y busco la info a mostrar
        dataInformes=new DataInformes();
        dataInformes.setTxt_detalles_informe(txt_detalles_informes);
        dataInformes.setTxt_cantidad_inscripciones(txt_cantidad_inscripciones);
        dataInformes.setTxt_valor_promedio(txt_valor_promedio);
        dataInformes.setTxt_actividad_filtrada(txt_actividad_filtrada);

        cargaInicialGeneral(dataInformes); //Función que hace llamado a todas las funciones para conseguir toda la data de los txt

        //Enlace click
        btnAplicarFiltros.setOnClickListener(this::OnClick_AplicarFiltros);
        btnBorrarFiltros.setOnClickListener(this::OnClick_BorrarFiltros);
        et_fecha_inicio.setOnClickListener(this::cargarFechaInicio);
        et_fecha_fin.setOnClickListener(this::cargarFechaFin);

        return view;
    }

    public void cargaInicialGeneral(DataInformes dataInformes){
        dataInformes.filtroCantidadInscripciones(null,null,null,null);
        dataInformes.filtroPromedioCalificaciones(null,null,null,null,ratingBar_promedio_calificacion);
    }
    public void cargarFechaInicio(View view){
        DatePickerDialog datePicker=new DatePickerDialog(getContext(),R.style.TemaDialogos, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int anio, int mes, int dia) {
                et_fecha_inicio.setText((anio+"-"+(mes+1)+"-"+dia).toString());
            }
        },2023,0,1);
        datePicker.show();
    }
    public void cargarFechaFin(View view){
        DatePickerDialog datePicker=new DatePickerDialog(getContext(),R.style.TemaDialogos, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int anio, int mes, int dia) {
                et_fecha_fin.setText((anio+"-"+(mes+1)+"-"+dia).toString());
            }
        },2025,0,1);
        datePicker.show();


    }
    public void OnClick_AplicarFiltros(View view){
        new AlertDialog.Builder(view.getContext())
                .setMessage("¿Desea modificar los filtros?")
                .setCancelable(false)
                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    if(spCategorias.getSelectedItem().toString()!="Cargando..."){
                        Categoria catSeleccionada=(Categoria) spCategorias.getSelectedItem();
                        String st_fechaInicio=et_fecha_inicio.getText().toString();
                        String st_fechaFin=et_fecha_fin.getText().toString();
                        Date fechaFin,fechaInicio;

                        if(!st_fechaInicio.isEmpty()){
                            fechaInicio=Date.valueOf(st_fechaInicio);
                        }else{
                            fechaInicio=null;
                        }
                        if(!st_fechaFin.isEmpty()){
                            fechaFin=Date.valueOf(st_fechaFin);
                        }else{
                            fechaFin=null;
                        }

                        //Si fueron ingresadas ambas fechas, me fijo que estén en el orden correcto
                        if(fechaInicio!=null && fechaFin!=null){
                            //Pregunto si fechaFin viene antes que fechaInicio, si es así, los invierto
                            if(fechaFin.before(fechaInicio)){
                                Date auxiliar=fechaFin;
                                fechaFin=fechaInicio;
                                fechaInicio=auxiliar;
                            }
                        }
                    /*if(catSeleccionada!=null){
                        if(catSeleccionada.getIdCategoria()!=0){

                        }
                    }*/
                        cargandoDialog dialogoCancelar=new cargandoDialog(getActivity());
                        dialogoCancelar.mostrarCargandoInforme(getParentFragmentManager());

                        dataInformes.filtroCantidadInscripciones(catSeleccionada,fechaInicio,fechaFin,null);
                        dataInformes.filtroPromedioCalificaciones(catSeleccionada,fechaInicio,fechaFin,null,ratingBar_promedio_calificacion);

                        Handler handler=new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialogoCancelar.cerrarDialogo();

                            }
                        },3000);

                        //Vaciamos los controles
                        et_fecha_inicio.setText("");
                        et_fecha_fin.setText("");}
                    }
                })
                .setNegativeButton("Sí", null)
                .show();
    }
    public void OnClick_BorrarFiltros(View view){
        cargaInicialGeneral(dataInformes);
    }
}

