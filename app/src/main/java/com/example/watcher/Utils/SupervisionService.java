package com.example.watcher.Utils;

import com.example.watcher.Model.ApiGeneralRespuesta;
import com.example.watcher.Model.Confirmacion;
import com.example.watcher.Model.Resultado;
import com.example.watcher.Model.Supervision;
import com.example.watcher.Model.SupervisionRespuesta;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface SupervisionService {

    @GET("supervision")
    Call<SupervisionRespuesta> getSupervisiones();

    @POST("supervision/confirmacion")
    Call<ApiGeneralRespuesta> postConfirmacion(@Body Confirmacion confirmacion);

    @POST("supervision/resultado")
    Call<ApiGeneralRespuesta> postResultado(@Body Resultado resultado);


}
