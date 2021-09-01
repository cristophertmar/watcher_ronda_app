package com.example.watcher;

import android.app.Application;

import com.example.watcher.Model.Usuario;

public class MyApplication extends Application {

    private Usuario usuario;

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

}
