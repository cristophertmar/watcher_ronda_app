package com.example.watcher.Model;

import java.io.Serializable;

public class AlarmaNotificacion implements Serializable {


    private String id;
    private String cod_abonado;
    private String abonado;
    private String lat;
    private String lng;
    private int id_estado;
    private String estado;
    private String fecha;
    private String hora;
    private int id_usuario;
    private String titulo;
    private String descripcion;

    public AlarmaNotificacion() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCod_abonado() {
        return cod_abonado;
    }

    public void setCod_abonado(String cod_abonado) {
        this.cod_abonado = cod_abonado;
    }

    public String getAbonado() {
        return abonado;
    }

    public void setAbonado(String abonado) {
        this.abonado = abonado;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public int getId_estado() {
        return id_estado;
    }

    public void setId_estado(int id_estado) {
        this.id_estado = id_estado;
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

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
