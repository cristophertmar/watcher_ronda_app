package com.example.watcher.Model;

public class LoginRespuesta {

    private boolean isSuccess;
    private String message;
    private Usuario data;
    private String token;
    private String expiration;

    public LoginRespuesta() {
    }

    public LoginRespuesta(boolean isSuccess, String message, Usuario data, String token, String expiration) {
        this.isSuccess = isSuccess;
        this.message = message;
        this.data = data;
        this.token = token;
        this.expiration = expiration;
    }

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

    public Usuario getData() {
        return data;
    }

    public void setData(Usuario data) {
        this.data = data;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }
}
