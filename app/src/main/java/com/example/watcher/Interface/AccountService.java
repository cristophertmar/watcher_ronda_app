package com.example.watcher.Interface;

import com.example.watcher.Model.Confirmacion;
import com.example.watcher.Model.LoginRespuesta;
import com.example.watcher.Model.Usuario;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AccountService {

    @POST("account/login")
    Call<LoginRespuesta> loguin(@Body Usuario usuario);

}
