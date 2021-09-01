package com.example.watcher.Model;

public class Resultado {

    private String resultado;
    private String comentario;
    private String tipo;
    private String id_gestion;

    public Resultado() {
    }

    public Resultado(String resultado, String comentario, String tipo, String id_gestion) {
        this.resultado = resultado;
        this.comentario = comentario;
        this.tipo = tipo;
        this.id_gestion = id_gestion;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getId_gestion() {
        return id_gestion;
    }

    public void setId_gestion(String id_gestion) {
        this.id_gestion = id_gestion;
    }
}
