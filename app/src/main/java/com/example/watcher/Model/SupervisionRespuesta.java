package com.example.watcher.Model;

import java.util.ArrayList;

public class SupervisionRespuesta {
    private ArrayList<Supervision> data;
    private int registros;

    public ArrayList<Supervision> getData() {
        return data;
    }

    public void setData(ArrayList<Supervision> data) {
        this.data = data;
    }

    public int getRegistros() {
        return registros;
    }

    public void setRegistros(int registros) {
        this.registros = registros;
    }
}
