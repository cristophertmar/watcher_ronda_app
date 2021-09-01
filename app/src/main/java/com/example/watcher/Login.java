package com.example.watcher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.watcher.Interface.AccountService;
import com.example.watcher.Model.LoginRespuesta;
import com.example.watcher.Model.SupervisionRespuesta;
import com.example.watcher.Model.Usuario;
import com.example.watcher.Utils.SupervisionService;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.watcher.Utils.Apis.URL_BASE;

public class Login extends AppCompatActivity {

    Button btnIngresar;
    private Retrofit retrofit;
    AccountService accountService;
    TextInputEditText txt_correo, txt_contrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );

        txt_correo = findViewById(R.id.txt_correo);
        txt_contrasena = findViewById(R.id.txt_contrasena);
        btnIngresar = findViewById(R.id.btnIngresar);

        retrofit = new Retrofit.Builder()
                .baseUrl(URL_BASE)
                .addConverterFactory( GsonConverterFactory.create() )
                .build();


        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String correo = txt_correo.getText().toString();
                String contrasena =txt_contrasena.getText().toString();

                if(correo.length() < 1) {
                    Toast.makeText(getApplicationContext(), "Ingrese su correo", Toast.LENGTH_SHORT).show(); return;
                } else if (contrasena.length() < 1) {
                    Toast.makeText(getApplicationContext(), "Ingrese su contraseña", Toast.LENGTH_SHORT).show(); return;
                }

                Usuario usuario = new Usuario( correo,  contrasena );

                accountService = retrofit.create( AccountService.class );
                Call<LoginRespuesta> call = accountService.loguin(usuario);

                call.enqueue( new Callback<LoginRespuesta>() {
                    @Override
                    public void onResponse(Call<LoginRespuesta> call, Response<LoginRespuesta> response) {
                        if(response.isSuccessful()) {
                            if(response.body().isSuccess()) {
                                guardar_usuario(response.body().getData());
                                Intent intent = new Intent().setClass(Login.this, Menu.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Correo o contraseña incorrecta", Toast.LENGTH_SHORT).show(); return;
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginRespuesta> call, Throwable t) {

                    }
                } );



            }
        });


    }


    private void guardar_usuario(Usuario usuario) {
        ((MyApplication) this.getApplication()).setUsuario(usuario);
    }
}