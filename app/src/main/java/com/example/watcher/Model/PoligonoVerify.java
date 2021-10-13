package com.example.watcher.Model;

public class PoligonoVerify {

    private int id_poligono;
    private int id_usuario;
    private double lat;
    private double lng;

    public PoligonoVerify(int id_poligono, int id_usuario, double lat, double lng) {
        this.id_poligono = id_poligono;
        this.id_usuario = id_usuario;
        this.lat = lat;
        this.lng = lng;
    }

    public int getId_poligono() {
        return id_poligono;
    }

    public void setId_poligono(int id_poligono) {
        this.id_poligono = id_poligono;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
