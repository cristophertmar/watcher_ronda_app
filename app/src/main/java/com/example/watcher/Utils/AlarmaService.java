package com.example.watcher.Utils;

import com.example.watcher.Model.AlarmaRespuesta;
import com.example.watcher.Model.ApiGeneralRespuesta;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AlarmaService {

    @GET("alarma/listar")
    Call<AlarmaRespuesta> getAlarma(@Query( "id_usuario" ) int id_usuario);

    @GET("alarma/actualizar_estado")
    Call<ApiGeneralRespuesta> putEstado(@Query( "id_alarma" ) String id_alarma, @Query( "id_estado" ) int id_estado);
}
