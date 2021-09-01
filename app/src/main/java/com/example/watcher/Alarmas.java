package com.example.watcher;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.watcher.Model.AlarmaRespuesta;
import com.example.watcher.Utils.AlarmaService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.watcher.Utils.Apis.URL_BASE;

public class Alarmas extends AppCompatActivity {

    private Retrofit retrofit;
    AlarmaService alarmaService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_alarmas );

        retrofit = new Retrofit.Builder()
                .baseUrl(URL_BASE)
                .addConverterFactory( GsonConverterFactory.create() )
                .build();

    }

    private void obtenerAlarma() {


    }


}