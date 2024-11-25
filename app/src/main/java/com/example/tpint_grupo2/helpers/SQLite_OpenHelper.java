package com.example.tpint_grupo2.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLite_OpenHelper extends SQLiteOpenHelper {

    // Nombre y versión de la base de datos
    private static final String DATABASE_NAME = "app_local.db";
    private static final int DATABASE_VERSION = 1;

    // Tabla usuarios
    private static final String TABLE_USUARIOS = "usuarios";
    private static final String COLUMN_ID = "nro_documento";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_CONECTADO = "conectado"; // Nuevo campo

    public SQLite_OpenHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear tabla usuarios
        String createUsuariosTable = "CREATE TABLE " + TABLE_USUARIOS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_PASSWORD + " TEXT NOT NULL, " +
                COLUMN_CONECTADO + " INTEGER DEFAULT 0" + // 0 = desconectado, 1 = conectado
                ");";
        db.execSQL(createUsuariosTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        onCreate(db);
    }

    // Método para abrir la base de datos en modo escritura
    public void abrir() {
        this.getWritableDatabase();
    }

    // Método para abrir la base de datos en modo lectura
    public void abrirLectura() {
        this.getReadableDatabase();
    }

    // Método para cerrar la base de datos
    public void cerrar() {
        this.close();
    }

    // Insertar usuario en la tabla local
    public void insertarUsuario(int nroDocumento, String password, boolean conectado) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, nroDocumento);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_CONECTADO, conectado ? 1 : 0);
        db.insert(TABLE_USUARIOS, null, values);
        db.close();
    }

    // Actualizar estado de conexión
    public void actualizarEstadoConexion(int nroDocumento, boolean conectado) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONECTADO, conectado ? 1 : 0);
        db.update(TABLE_USUARIOS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(nroDocumento)});
        db.close();
    }

    // Validar si el usuario existe con el estado de conexión
    public boolean validarUsuarioLocal(int nroDocumento, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USUARIOS + " WHERE " +
                COLUMN_ID + " = ? AND " + COLUMN_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(nroDocumento), password});

        boolean existe = cursor.moveToFirst(); // Si hay al menos un registro
        cursor.close();
        db.close();
        return existe;
    }

    // Obtener estado de conexión de un usuario
    public boolean obtenerEstadoConexion(int nroDocumento) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_CONECTADO + " FROM " + TABLE_USUARIOS +
                " WHERE " + COLUMN_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(nroDocumento)});

        boolean conectado = false;
        if (cursor.moveToFirst()) {
            conectado = cursor.getInt(cursor.getColumnIndex(COLUMN_CONECTADO)) == 1;
        }
        cursor.close();
        db.close();
        return conectado;
    }

    public String[] obtenerUsuarioActivo() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] datosUsuario = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT nro_documento, password FROM usuarios WHERE conectado = 1", null);
            if (cursor.moveToFirst()) {
                String nroDocumento = cursor.getString(0);
                String contrasena = cursor.getString(1);
                datosUsuario = new String[]{nroDocumento, contrasena};
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return datosUsuario;
    }

    public void actualizarEstadoUsuarioActivo(int nuevoEstado) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("conectado", nuevoEstado);

        db.update("usuarios", values, "conectado = ?", new String[]{"1"});
        db.close();
    }
}

