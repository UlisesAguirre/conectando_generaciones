package com.example.tpint_grupo2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.tpint_grupo2.conexion.DataActividadesACalificar;
import com.example.tpint_grupo2.conexion.DataMisSolicitudes;
import com.example.tpint_grupo2.entidades.Usuario;
import com.example.tpint_grupo2.enums.EnumEstadosActividades;
import com.example.tpint_grupo2.sessions.UsuariosSession;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalificarActividadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalificarActividadFragment extends Fragment {

    private int idActividad;
    private String nombreActividad;
    private String fechaActividad;
    private String organizador;
    private String comentario;
    private float calificacion;

    private TextView tv_nombre_actividad;
    private TextView tv_fecha_actividad;
    private TextView tv_organizador;
    private EditText et_comentario;
    private RatingBar rb_calificacion;
    private Button btn_aceptar;
    private TextView tv_comentario;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CalificarActividadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CalificarActividadFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CalificarActividadFragment newInstance(String param1, String param2) {
        CalificarActividadFragment fragment = new CalificarActividadFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idActividad = getArguments().getInt("idActividad");
            nombreActividad = getArguments().getString("nombreActividad");
            organizador = getArguments().getString("organizador");
            fechaActividad = getArguments().getString("fechaActividad");

        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calificar_actividad, container, false);

        tv_nombre_actividad = view.findViewById(R.id.nombre_actividad);
        tv_fecha_actividad = view.findViewById(R.id.fecha_actividad);
        tv_organizador = view.findViewById(R.id.organizar_actividad);
        et_comentario = view.findViewById(R.id.comentarios_actividad);
        rb_calificacion = view.findViewById(R.id.rating_bar_calificacion);
        btn_aceptar = view.findViewById(R.id.btn_calificar_calificar);

        tv_nombre_actividad.setText("Actividad: " + nombreActividad);
        tv_fecha_actividad.setText("Fecha: " + fechaActividad);
        tv_organizador.setText("Organizador: " + organizador);

        int backStackCount = getParentFragmentManager().getBackStackEntryCount();
        Log.d("BackStackCount", "Current back stack count: " + backStackCount);

        DataActividadesACalificar service = new DataActividadesACalificar();


                btn_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                calificacion = rb_calificacion.getRating();
                comentario = et_comentario.getText().toString();

                if (TextUtils.isEmpty(comentario) || calificacion == 0) {
                    new AlertDialog.Builder(view.getContext())
                            .setMessage("Los campos son obligatorios. Intente nuevamente.")
                            .setCancelable(false)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                } else {
                    UsuariosSession userSession = new UsuariosSession(getContext());
                    Usuario usuario = userSession.getUser();


                    service.UpdateEstadoActividad(EnumEstadosActividades.CALIFICADA, idActividad, usuario);

                    service.CalificarActividad(idActividad,calificacion,comentario, usuario);

                    new AlertDialog.Builder(view.getContext())
                            .setMessage("Actividad calificada correctamente")
                            .setCancelable(false)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    getParentFragmentManager().popBackStack();
                                }
                            })
                            .show();
                }

            }

        } );


        return view;
    }
}