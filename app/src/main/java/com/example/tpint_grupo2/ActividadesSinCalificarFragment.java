package com.example.tpint_grupo2;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tpint_grupo2.conexion.DataActividadesACalificar;
import com.example.tpint_grupo2.entidades.ActividadAgenda;
import com.example.tpint_grupo2.entidades.Usuario;
import com.example.tpint_grupo2.sessions.UsuariosSession;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ActividadesSinCalificarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActividadesSinCalificarFragment extends Fragment {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ActividadesSinCalificarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ActividadesSinCalificarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ActividadesSinCalificarFragment newInstance(String param1, String param2) {
        ActividadesSinCalificarFragment fragment = new ActividadesSinCalificarFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_actividades_sin_calificar, container, false);

        UsuariosSession userSession = new UsuariosSession(getContext());
        Usuario usuario = userSession.getUser();


        DataActividadesACalificar service = new DataActividadesACalificar();
        service.GetAllActividades(view, this, usuario);

        return view;
    }
}