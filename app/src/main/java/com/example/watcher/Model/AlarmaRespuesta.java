package com.example.watcher.Model;

import java.util.ArrayList;

public class AlarmaRespuesta {

    private boolean isSuccess;
    private String message;
    private AlarmaNotificacion data;
    private int registros;

    public AlarmaRespuesta() {
    }

    public AlarmaRespuesta(boolean isSuccess, String message, AlarmaNotificacion data, int registros) {
        this.isSuccess = isSuccess;
        this.message = message;
        this.data = data;
        this.registros = registros;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AlarmaNotificacion getData() {
        return data;
    }

    public void setData(AlarmaNotificacion data) {
        this.data = data;
    }

    public int getRegistros() {
        return registros;
    }

    public void setRegistros(int registros) {
        this.registros = registros;
    }
}
