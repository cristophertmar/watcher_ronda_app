package com.example.watcher.Model;

import java.io.Serializable;

public class Supervision implements Serializable {

    private String id;
    private String abonado;
    private String estado;
    private String fecha;
    private String hora;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAbonado() {
        return abonado;
    }

    public void setAbonado(String abonado) {
        this.abonado = abonado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }
}
