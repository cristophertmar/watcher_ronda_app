package com.example.watcher.Model;

public class ImagenRespuesta {

    private boolean key;
    private String value;

    public ImagenRespuesta() {
    }

    public ImagenRespuesta(boolean key, String value) {
        this.key = key;
        this.value = value;
    }

    public boolean isKey() {
        return key;
    }

    public void setKey(boolean key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
