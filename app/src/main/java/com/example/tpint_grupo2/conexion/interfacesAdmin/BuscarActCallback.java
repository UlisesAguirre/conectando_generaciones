package com.example.tpint_grupo2.conexion.interfacesAdmin;

import com.example.tpint_grupo2.entidades.Actividad;

import java.util.List;

public interface BuscarActCallback {
    void onBuscarSuccess(List<Actividad> actividades);
    void onBuscarError(String error);
}
