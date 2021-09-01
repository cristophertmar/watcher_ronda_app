package com.example.watcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.watcher.Model.AlarmaNotificacion;
import com.example.watcher.Model.AlarmaRespuesta;
import com.example.watcher.Model.ApiGeneralRespuesta;
import com.example.watcher.Services.CoordenadaService;
import com.example.watcher.Utils.AlarmaService;

import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.watcher.Utils.Apis.URL_BASE;

public class Menu extends AppCompatActivity {

    private CardView cv_supervisiones, cv_alarmas;
    private Retrofit retrofit;
    AlarmaService alarmaService;
    AlarmaRespuesta alarmaRespuestaRecibida;
    AlarmaNotificacion alarma;
    ProgressDialog progressDialog;
    Dialog dialog;
    int id_usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_menu );

        cv_supervisiones = findViewById(R.id.cv_supervisiones);
        cv_alarmas = findViewById(R.id.cv_alarmas);

        id_usuario = ((MyApplication) this.getApplication()).getUsuario().getId();

        retrofit = new Retrofit.Builder()
                .baseUrl(URL_BASE)
                .addConverterFactory( GsonConverterFactory.create() )
                .build();

        obtenerAlarma();
        startService( new Intent(getApplicationContext(), CoordenadaService.class ) );

        cv_supervisiones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, Supervisiones.class);
                startActivity(intent);
            }
        });

        cv_alarmas.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(alarmaRespuestaRecibida != null) {
                    if(alarmaRespuestaRecibida.isSuccess()) {

                        dialog = new Dialog( Menu.this );
                        dialog.setContentView(R.layout.custom_dialog);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_background));
                        }
                        dialog.getWindow().setLayout( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.setCancelable(false); //Optional
                        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog

                        Button Okay = dialog.findViewById(R.id.btn_okay);
                        Button Cancel = dialog.findViewById(R.id.btn_cancel);

                        Okay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Toast.makeText(Menu.this, "Okay", Toast.LENGTH_SHORT).show();

                                alarma = alarmaRespuestaRecibida.getData();

                                alarmaService = retrofit.create( AlarmaService.class );
                                Call<ApiGeneralRespuesta> call = alarmaService.putEstado(alarma.getId(), 2);
                                call.enqueue( new Callback<ApiGeneralRespuesta>() {
                                    @Override
                                    public void onResponse(Call<ApiGeneralRespuesta> call, Response<ApiGeneralRespuesta> response) {
                                        if(response.isSuccessful()) {
                                            Intent intent = new Intent(Menu.this, DestinoRonda.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putSerializable("ALARMA", alarma );
                                            intent.putExtra("OPERACION", "ALARMA");
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                            dialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ApiGeneralRespuesta> call, Throwable t) {

                                    }
                                } );



                            }
                        });

                        Cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        dialog.show();



                    } else {
                        Toast.makeText(getApplicationContext(), "No tiene alarma asignada", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } );

    }


    private void obtenerAlarma() {

        mostrarProgress();

        int id_usuario = ((MyApplication) this.getApplication()).getUsuario().getId();

        alarmaService = retrofit.create( AlarmaService.class );
        Call<AlarmaRespuesta> call = alarmaService.getAlarma(id_usuario);
        call.enqueue( new Callback<AlarmaRespuesta>() {
            @Override
            public void onResponse(Call<AlarmaRespuesta> call, Response<AlarmaRespuesta> response) {
                if (response.isSuccessful()) {
                    alarmaRespuestaRecibida = response.body();
                    ocultarProgress();
                }
            }

            @Override
            public void onFailure(Call<AlarmaRespuesta> call, Throwable t) {

            }
        } );


    }

    public void mostrarProgress() {
        // Inicalizar progress
        progressDialog = new ProgressDialog( this );
        // Mostrar dialog
        progressDialog.show();
        // Set content View
        progressDialog.setContentView( R.layout.progress_dialog );
        // Set transparencia de fondo
        progressDialog.getWindow().setBackgroundDrawableResource( android.R.color.transparent  );
    }

    public void ocultarProgress() {
        //Dismmiss
        progressDialog.dismiss();
    }

}