package com.example.tpint_grupo2.elementos;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tpint_grupo2.FragmentMenuInformes;
import com.example.tpint_grupo2.R;

public class cargandoDialog  {

    Activity activity;
    AlertDialog dialogo;

    public cargandoDialog(Activity context) {
        this.activity =context;
    }

    public void mostrarCargandoInforme(FragmentManager FragmentManager){
        Button btnCancelarInforme;
        TextView txt_mensaje;

        AlertDialog.Builder builder=new AlertDialog.Builder(activity);

        LayoutInflater inflater= activity.getLayoutInflater();
        View view= inflater.inflate(R.layout.cargando_dialog,null);
        txt_mensaje=view.findViewById(R.id.txt_mensaje_dialogo);
        btnCancelarInforme=view.findViewById(R.id.btnCancelarInforme);

        txt_mensaje.setText("Generando Informe");
        btnCancelarInforme.setText("Cancelar Informe");
        btnCancelarInforme.setVisibility(View.VISIBLE);
        btnCancelarInforme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentMenuInformes objMenuInformes=new FragmentMenuInformes();
                FragmentManager fragmentManager = FragmentManager;
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_menu_principal_container, objMenuInformes);
                transaction.addToBackStack(null);
                transaction.commit();

                dialogo.dismiss();
            }
        });

        builder.setView(view);
        builder.setCancelable(false);


        dialogo=builder.create();
        dialogo.show();

    }

    public void mostrarCargandoMensaje(){
        TextView txt_mensaje;

        AlertDialog.Builder builder=new AlertDialog.Builder(activity);

        LayoutInflater inflater= activity.getLayoutInflater();
        View view= inflater.inflate(R.layout.cargando_dialog_sin_btn,null);
        txt_mensaje=view.findViewById(R.id.txt_mensaje_dialogo);

        txt_mensaje.setText("Cargando...");

        builder.setView(view);
        builder.setCancelable(false);

        dialogo=builder.create();
        dialogo.show();

    }

    public void cerrarDialogo(){
        dialogo.dismiss();
    }
}

