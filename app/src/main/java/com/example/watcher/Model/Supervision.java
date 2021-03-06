package com.example.watcher.Model;

import java.io.Serializable;

public class Supervision implements Serializable {

    private String id;
    private String cod_abonado;
    private String abonado;
    private String lat;
    private String lng;
    private int id_estado;
    private String estado;
    private String fecha;
    private String hora;
    private int id_riesgo;
    private String riesgo;
    private int id_usuario;

    public Supervision() {
    }

    public Supervision(String id, String cod_abonado, String abonado, String lat, String lng, int id_estado, String estado, String fecha, String hora, int id_riesgo, String riesgo, int id_usuario) {
        this.id = id;
        this.cod_abonado = cod_abonado;
        this.abonado = abonado;
        this.lat = lat;
        this.lng = lng;
        this.id_estado = id_estado;
        this.estado = estado;
        this.fecha = fecha;
        this.hora = hora;
        this.id_riesgo = id_riesgo;
        this.riesgo = riesgo;
        this.id_usuario = id_usuario;
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

    public int getId_riesgo() {
        return id_riesgo;
    }

    public void setId_riesgo(int id_riesgo) {
        this.id_riesgo = id_riesgo;
    }

    public String getRiesgo() {
        return riesgo;
    }

    public void setRiesgo(String riesgo) {
        this.riesgo = riesgo;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }
}
