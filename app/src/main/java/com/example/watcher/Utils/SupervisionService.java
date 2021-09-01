package com.example.watcher.Utils;

import com.example.watcher.Model.ApiGeneralRespuesta;
import com.example.watcher.Model.Confirmacion;
import com.example.watcher.Model.ImagenRespuesta;
import com.example.watcher.Model.Resultado;
import com.example.watcher.Model.Supervision;
import com.example.watcher.Model.SupervisionRespuesta;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface SupervisionService {

    @GET("supervision/listar")
    Call<SupervisionRespuesta> getSupervisiones(@Query( "id_usuario" ) int id_usuario);

    @GET("supervision/actualizar_estado")
    Call<ApiGeneralRespuesta> putEstado(@Query( "id_supervision" ) String id_supervision, @Query( "id_estado" ) int id_estado);

    @POST("supervision/confirmacion")
    Call<ApiGeneralRespuesta> postConfirmacion(@Body Confirmacion confirmacion);

    @POST("informe/guardar")
    Call<ApiGeneralRespuesta> postResultado(@Body Resultado resultado);

    @Multipart
    @POST("FileUpload")
    Call<ImagenRespuesta> cargarImagen(@Part MultipartBody.Part file);


}
