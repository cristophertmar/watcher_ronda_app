package com.example.watcher.Model;

public class Confirmacion {
    private boolean estado_proceso;
    private String fecha_hora;
    private int tipo_atencion;
    private String cod_abonado;
    private String id_motorizado;

    public Confirmacion() {
    }

    public Confirmacion(boolean estado_proceso, String fecha_hora, int tipo_atencion, String cod_abonado, String id_motorizado) {
        this.estado_proceso = estado_proceso;
        this.fecha_hora = fecha_hora;
        this.tipo_atencion = tipo_atencion;
        this.cod_abonado = cod_abonado;
        this.id_motorizado = id_motorizado;
    }

    public boolean isEstado_proceso() {
        return estado_proceso;
    }

    public void setEstado_proceso(boolean estado_proceso) {
        this.estado_proceso = estado_proceso;
    }

    public String getFecha_hora() {
        return fecha_hora;
    }

    public void setFecha_hora(String fecha_hora) {
        this.fecha_hora = fecha_hora;
    }

    public int getTipo_atencion() {
        return tipo_atencion;
    }

    public void setTipo_atencion(int tipo_atencion) {
        this.tipo_atencion = tipo_atencion;
    }

    public String getCod_abonado() {
        return cod_abonado;
    }

    public void setCod_abonado(String cod_abonado) {
        this.cod_abonado = cod_abonado;
    }

    public String getId_motorizado() {
        return id_motorizado;
    }

    public void setId_motorizado(String id_motorizado) {
        this.id_motorizado = id_motorizado;
    }
}
