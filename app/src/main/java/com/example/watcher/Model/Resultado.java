package com.example.watcher.Model;

public class Resultado {

    private int resultado;
    private String comentario;

    public Resultado() {
    }

    public Resultado(int resultado, String comentario) {
        this.resultado = resultado;
        this.comentario = comentario;
    }

    public int getResultado() {
        return resultado;
    }

    public void setResultado(int resultado) {
        this.resultado = resultado;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
