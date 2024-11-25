package com.example.tpint_grupo2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tpint_grupo2.conexion.DataMisSolicitudes;
import com.example.tpint_grupo2.entidades.Usuario;
import com.example.tpint_grupo2.sessions.UsuariosSession;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MisSolicitudesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MisSolicitudesFragment extends Fragment {

    private RecyclerView rvSolicitudes;
    private SolicitudAdapter solicitudAdapter;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MisSolicitudesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MisSolitudesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MisSolicitudesFragment newInstance(String param1, String param2) {
        MisSolicitudesFragment fragment = new MisSolicitudesFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mis_solicitudes, container, false);

        UsuariosSession userSession = new UsuariosSession(getContext());
        Usuario usuario = userSession.getUser();

        DataMisSolicitudes service = new DataMisSolicitudes();
        service.GetAllSolicitudes(view, usuario);




        return view;
    }
}