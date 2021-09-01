package com.example.watcher.Utils;

public class Apis {

    //public static final String URL_BASE = "http://192.168.0.4/watcher/api/";
    //public static final String URL_HUB = "http://192.168.0.4/watcher/";
    public static final String URL_HUB = "http://190.117.64.238/wronda/";
    public static final String URL_BASE = "http://190.117.64.238/wronda/api/";
    public static SupervisionService getSupervisionService() {
        return Cliente.getCliente(URL_BASE).create(SupervisionService.class);
    }



}
