package com.cobertura360.app

import android.app.Application
import android.util.Log

class CobertuApp : Application() {

    override fun onCreate() {
        super.onCreate()
        try {
            Log.d("CobertuApp", "Aplicación iniciada")
            // Inicialización de BD y dependencias será aquí
        } catch (e: Exception) {
            Log.e("CobertuApp", "Error al inicializar app", e)
        }
    }
}
