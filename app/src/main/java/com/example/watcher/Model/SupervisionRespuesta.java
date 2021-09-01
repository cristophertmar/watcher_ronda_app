package com.example.watcher.Model;

import java.util.ArrayList;

public class SupervisionRespuesta {
    private boolean isSuccess;
    private String message;
    private ArrayList<Supervision> data;
    private int total;

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

    public ArrayList<Supervision> getData() {
        return data;
    }

    public void setData(ArrayList<Supervision> data) {
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
