package com.example.tpint_grupo2;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tpint_grupo2.conexion.DataActividadesAdmin;
import com.example.tpint_grupo2.conexion.interfacesAdmin.DeleteLogicoActCallback;

public class FragmentEliminarActividad extends Fragment {

    private DataActividadesAdmin dataActividades;
    private int actividadId;
    private TextView textoEliminar;
    private Button btnAceptar, btnCancelar;

    public FragmentEliminarActividad() {}

    public static FragmentEliminarActividad newInstance() {
        FragmentEliminarActividad fragment = new FragmentEliminarActividad();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_eliminar_actividad, container, false);

        dataActividades = new DataActividadesAdmin();
        textoEliminar = view.findViewById(R.id.tv_eliminar_actividad);
        btnCancelar = view.findViewById(R.id.btn_cancelar_eliminar);
        btnAceptar = view.findViewById(R.id.btn_aceptar_eliminar);

        if (getArguments() != null) {
            String actividadIdString = getArguments().getString("actividad_id");
            String actividadNombre = getArguments().getString("actividad_nombre");

            try {
                actividadId = Integer.parseInt(actividadIdString);
                textoEliminar.setText("¿Esta seguro/a de eliminar " + actividadNombre + "?");
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        btnCancelar.setOnClickListener(v -> volverALaVistaAnterior());
        btnAceptar.setOnClickListener(v -> eliminarActividadLogica(dataActividades, actividadId));

        return view;
    }

    private void eliminarActividadLogica(DataActividadesAdmin data, int actividadId) {
        data.bajaLogicaActividad(actividadId, new DeleteLogicoActCallback() {
            @Override
            public void onDeleteSuccess() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), "Actividad borrada correctamente.", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onDeleteError(String s) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), "Error al borrar actividad: " + s, Toast.LENGTH_LONG).show();
                    });
                }
            }
        });

        volverALaVistaAnterior();
    }

    private void volverALaVistaAnterior() {
        FragmentManager fragmentManager = getParentFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        }
    }
}