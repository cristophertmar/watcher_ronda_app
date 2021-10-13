package com.example.watcher;

import android.app.Application;

import com.example.watcher.Model.AlarmaNotificacion;
import com.example.watcher.Model.Usuario;

public class MyApplication extends Application {

    private Usuario usuario;
    private AlarmaNotificacion alarma;

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public AlarmaNotificacion getAlarma() {
        return alarma;
    }

    public void setAlarma(AlarmaNotificacion alarma) {
        this.alarma = alarma;
    }
}
