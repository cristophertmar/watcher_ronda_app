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

public interface SupervisionService {

    @GET("supervision")
    Call<SupervisionRespuesta> getSupervisiones();

    @POST("supervision/confirmacion")
    Call<ApiGeneralRespuesta> postConfirmacion(@Body Confirmacion confirmacion);

    @POST("supervision/resultado")
    Call<ApiGeneralRespuesta> postResultado(@Body Resultado resultado);

    @Multipart
    @POST("FileUpload")
    Call<ImagenRespuesta> cargarImagen(@Part MultipartBody.Part file);


}
