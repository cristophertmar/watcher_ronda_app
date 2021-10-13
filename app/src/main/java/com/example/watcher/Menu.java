package com.example.watcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.watcher.Model.AlarmaNotificacion;
import com.example.watcher.Model.AlarmaRespuesta;
import com.example.watcher.Model.ApiGeneralRespuesta;
import com.example.watcher.Model.PoligonoVerify;
import com.example.watcher.Model.Usuario;
import com.example.watcher.Services.CoordenadaService;
import com.example.watcher.Services.GpsTrackerAlarmReceiver;
import com.example.watcher.Utils.AlarmaService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;

import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.watcher.Utils.Apis.URL_BASE;
import static com.example.watcher.Utils.Apis.URL_HUB;

public class Menu extends AppCompatActivity {

    private CardView cv_supervisiones, cv_alarmas;
    private Retrofit retrofit;
    AlarmaService alarmaService;
    AlarmaRespuesta alarmaRespuestaRecibida;
    AlarmaNotificacion alarma;
    ProgressDialog progressDialog;
    Dialog dialog;
    int id_usuario;
    Handler handler = new Handler();
    private FusedLocationProviderClient mFusedLocationClient;

    private final int TIEMPO = 5000;


    private GoogleMap mMap;

    HubConnection hubConnection;
    private AlarmManager alarmManager;
    private Intent gpsTrackerIntent;
    private PendingIntent pendingIntent;

    TextView tv_cant_alarmas, tv_cant_supervisiones, tv_cerrar_session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_menu );

        cv_supervisiones = findViewById(R.id.cv_supervisiones);
        cv_alarmas = findViewById(R.id.cv_alarmas);

        tv_cant_alarmas = findViewById(R.id.tv_cant_alarmas);
        tv_cant_supervisiones = findViewById(R.id.tv_cant_supervisiones);
        tv_cerrar_session = findViewById(R.id.tv_cerrar_session);

        id_usuario = ((MyApplication) this.getApplication()).getUsuario().getId();

        retrofit = new Retrofit.Builder()
                .baseUrl(URL_BASE)
                .addConverterFactory( GsonConverterFactory.create() )
                .build();

        obtenerAlarma();
        startService( new Intent(getApplicationContext(), CoordenadaService.class ) );


        //Crear hubconnection
        hubConnection = HubConnectionBuilder.create( URL_HUB + "poligonohub" ).build();
        hubConnection.start();

        /*hubConnection.on( "recibirAlarma", (alarma) -> {
            if(alarma.getId_usuario() == ((MyApplication) this.getApplication()).getUsuario().getId()) {
                obtenerAlarma();
                test();
            }
        }, AlarmaNotificacion.class );*/


        cv_supervisiones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, Supervisiones.class);
                startActivity(intent);
                finish();
            }
        });

        tv_cerrar_session.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, Login.class);
                startActivity(intent);
                finish();
            }
        } );

        cv_alarmas.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(alarmaRespuestaRecibida.getData().getAlarmas() > 0) {

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
                                        finish();
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
                    Toast.makeText(getApplicationContext(), "Usted no tiene una alarma asignada", Toast.LENGTH_SHORT).show();
                }
            }
        } );

        //mFusedLocationClient = LocationServices.getFusedLocationProviderClient( this );
        //test();

        startService( new Intent(getApplicationContext(), CoordenadaService.class ) );

        permisosGPS();

    }

    private void permisosGPS() {

        /*******************************GPS UBICACION****************************************/
        int permissionCheck = ContextCompat.checkSelfPermission(Menu.this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Menu.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(Menu.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
        }
        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
            startAlarmManager();
        }
    }

    private void startAlarmManager() {
        //Log.d(TAG, "startAlarmManager");
        Context context = getBaseContext();
        //Log.d("StartAlarma", "iniciarAlarma");
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        gpsTrackerIntent = new Intent(context, GpsTrackerAlarmReceiver.class);
        //gpsTrackerIntent.setAction("pe.com.zisac.action.close");
        pendingIntent = PendingIntent.getBroadcast(context, 0, gpsTrackerIntent, 0);

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                5000,//1000 * 10, // 60000 = 1 minute
                pendingIntent);
    }

    @Override
    public void onDestroy() {
        hubConnection.stop();
        super.onDestroy();
    }

    private void enviarCoordenadas() {
        if (ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding

            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener( this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Log.e("Latitud: ", + location.getLatitude() + "Longitude: " + location.getLongitude());
                            Toast.makeText(getApplicationContext(), "Latitud: " + location.getLatitude() + " Longitude: " + location.getLongitude(), Toast.LENGTH_SHORT).show();

                            PoligonoVerify poligono = new PoligonoVerify( 1,1, location.getLatitude(),  location.getLongitude());

                            if(hubConnection.getConnectionState() == HubConnectionState.CONNECTED){
                                hubConnection.send( "enviarCoordenadasPoligono",  poligono);
                            }
                        }
                    }
                } );
    }

    public void test() {
        handler.postDelayed(new Runnable() {
            public void run() {

                // función a ejecutar
                //Toast.makeText(getApplicationContext(), "Este es un test", Toast.LENGTH_SHORT).show(); // función para refrescar la ubicación del conductor, creada en otra línea de código
                enviarCoordenadas();

                handler.postDelayed(this, TIEMPO);
            }

        }, TIEMPO);

    }

    public void  obtenerAlarma() {

        mostrarProgress();

        int id_usuario = ((MyApplication) this.getApplication()).getUsuario().getId();

        alarmaService = retrofit.create( AlarmaService.class );
        Call<AlarmaRespuesta> call = alarmaService.getAlarma(id_usuario);
        call.enqueue( new Callback<AlarmaRespuesta>() {
            @Override
            public void onResponse(Call<AlarmaRespuesta> call, Response<AlarmaRespuesta> response) {
                if (response.isSuccessful()) {
                    ocultarProgress();
                    alarmaRespuestaRecibida = response.body();
                    alarma = alarmaRespuestaRecibida.getData();
                    guardar_alarma(alarma);
                    tv_cant_alarmas.setText( alarma.getAlarmas() + "" );
                    tv_cant_supervisiones.setText( alarma.getSupervisiones() + "" );
                }
            }

            @Override
            public void onFailure(Call<AlarmaRespuesta> call, Throwable t) {
                ocultarProgress();
            }
        } );


    }

    private void guardar_alarma(AlarmaNotificacion alarma) {
        ((MyApplication) this.getApplication()).setAlarma(alarma);
    }

    private AlarmaNotificacion obtener_alarma() {
        return ((MyApplication) this.getApplication()).getAlarma();
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