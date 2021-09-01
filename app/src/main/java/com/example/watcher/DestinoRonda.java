package com.example.watcher;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.watcher.Model.AlarmaNotificacion;
import com.example.watcher.Model.ApiGeneralRespuesta;
import com.example.watcher.Model.SupervisionRespuesta;
import com.example.watcher.Services.CoordenadaService;
import com.example.watcher.Utils.AlarmaService;
import com.example.watcher.Utils.SupervisionService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.watcher.Utils.Apis.URL_BASE;
import static com.example.watcher.Utils.Apis.URL_HUB;

public class DestinoRonda extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    AlertDialog alerta = null;
    HubConnection hubConnection;
    private FusedLocationProviderClient mFusedLocationClient;
    com.example.watcher.Model.Supervision supervision;
    AlarmaNotificacion alarma;
    Button btn_atencion, btn_informe;
    private int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
    double lat_destino;
    double lng_destino;
    String OPERACION;

    private Retrofit retrofit;
    SupervisionService supervisionService;
    AlarmaService alarmaService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_destino_ronda );

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById( R.id.map );
        mapFragment.getMapAsync( this );

        retrofit = new Retrofit.Builder()
                .baseUrl(URL_BASE)
                .addConverterFactory( GsonConverterFactory.create() )
                .build();

        Bundle objetoEnviado = getIntent().getExtras();
        OPERACION = getIntent().getStringExtra("OPERACION");
        supervision = null;

        btn_atencion = findViewById(R.id.btn_atencion);

        btn_informe = findViewById(R.id.btn_informe);

        if(objetoEnviado != null) {

            switch (OPERACION) {
                case "SUPERVISION":
                    supervision = (com.example.watcher.Model.Supervision) objetoEnviado.getSerializable("SUPERVISION");
                    lat_destino = Double.parseDouble( supervision.getLat() ) ;
                    lng_destino = Double.parseDouble( supervision.getLng() ) ;
                    break;
                default:
                    alarma = (AlarmaNotificacion) objetoEnviado.getSerializable("ALARMA");
                    lat_destino =  Double.parseDouble( alarma.getLat() );
                    lng_destino = Double.parseDouble( alarma.getLng() );
                    btn_atencion.setVisibility( View.GONE );
                    btn_informe.setVisibility( View.VISIBLE );
                    break;
            }

        }

        //Crear hubconnection
        hubConnection = HubConnectionBuilder.create( URL_HUB + "coordenadahub" ).build();
        hubConnection.start();

        getAlertaNotGps();
        /*getLocation();*/

        final Button btn_satelite = findViewById(R.id.btn_satelite);
        btn_satelite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /*btn_satelite.setVisibility(View.GONE);*/
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });

        final Button btn_hibrido = findViewById(R.id.btn_hibrido);
        btn_hibrido.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        });

        final Button btn_Normal = findViewById(R.id.btn_Normal);
        btn_Normal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });

        btn_atencion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                switch (OPERACION) {
                    case "SUPERVISION":
                        actualizar_estado_supervision(2);
                        break;
                    default:
                        break;
                }

                btn_informe.setVisibility( View.VISIBLE );
                btn_atencion.setVisibility( View.GONE );
                subirLatLongHub();
                //startService( new Intent(getApplicationContext(), CoordenadaService.class ) );

            }
        });

        btn_informe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent().setClass(DestinoRonda.this, Supervision.class);
                Bundle bundle = new Bundle();

                switch (OPERACION) {
                    case "SUPERVISION":
                        actualizar_estado_supervision(3);
                        bundle.putSerializable("supervision", supervision);
                        intent.putExtra("OPERACION", "SUPERVISION");
                        break;
                    default:
                        actualizar_estado_alarma(3);
                        bundle.putSerializable("alarma", alarma);
                        intent.putExtra("OPERACION", "ALARMA");
                        break;
                }
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient( this );


    }

    private void actualizar_estado_supervision(int id_estado) {

        supervisionService = retrofit.create( SupervisionService.class );
        Call<ApiGeneralRespuesta> call = supervisionService.putEstado(supervision.getId(), id_estado);
        call.enqueue( new Callback<ApiGeneralRespuesta>() {
            @Override
            public void onResponse(Call<ApiGeneralRespuesta> call, Response<ApiGeneralRespuesta> response) {

            }

            @Override
            public void onFailure(Call<ApiGeneralRespuesta> call, Throwable t) {

            }
        } );

    }

    private  void actualizar_estado_alarma(int id_estado) {

        alarmaService = retrofit.create( AlarmaService.class );
        Call<ApiGeneralRespuesta> call = alarmaService.putEstado(alarma.getId(), 3);
        call.enqueue( new Callback<ApiGeneralRespuesta>() {
            @Override
            public void onResponse(Call<ApiGeneralRespuesta> call, Response<ApiGeneralRespuesta> response) {

            }

            @Override
            public void onFailure(Call<ApiGeneralRespuesta> call, Throwable t) {

            }
        } );

    }


    private void subirLatLongHub() {
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
                            enviarCoordenadas(location.getLatitude(), location.getLongitude(), 1);
                        }
                    }
                } );
    }

    @Override
    public void onDestroy() {
        hubConnection.stop();
        super.onDestroy();
    }

    private void getAlertaNotGps() {

        /*final AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setMessage( "Por favor encienda el GPS. Desea activarlo?" )
                .setCancelable( false )
                .setTitle( "GPS" )
                .setPositiveButton( "Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity( new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS ) );
                    }
                } ).setNegativeButton( "No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                dialog.cancel();
            }
        } );
        alerta = builder.create();
        alerta.show();*/
    }

    public void enviarCoordenadas(double lat, double lng, int id_motorizado) {
        if(hubConnection.getConnectionState() == HubConnectionState.CONNECTED){
            hubConnection.send( "enviarCoordenada",  lat, lng, id_motorizado);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng destino = new LatLng( lat_destino, lng_destino );
        mMap.addMarker( new MarkerOptions().position( destino ).title( "Destino" ) );
        mMap.moveCamera( CameraUpdateFactory.newLatLng( destino ) );
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target( destino )
                .zoom( 12 )
                .bearing( 90 )
                .tilt( 45 )
                .build();
        mMap.animateCamera( CameraUpdateFactory.newCameraPosition( cameraPosition ) );

        if (ActivityCompat.checkSelfPermission( this,
                Manifest.permission.ACCESS_FINE_LOCATION )
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION )
                != PackageManager.PERMISSION_GRANTED) {
                /*getLastLocation();*/

                ActivityCompat.requestPermissions( DestinoRonda.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }

        mMap.setMyLocationEnabled( true );
        mMap.getUiSettings().setMyLocationButtonEnabled( false );
        LocationManager locationManager = (LocationManager) DestinoRonda.this.getSystemService( Context.LOCATION_SERVICE );
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLng miUbicacion = new LatLng( location.getLatitude(), location.getLongitude() );
                //Toast.makeText(getApplicationContext(), "Cambiando ubicación", Toast.LENGTH_SHORT).show();
                mMap.addMarker( new MarkerOptions().position( miUbicacion ).title( "Motorizado" ));
                enviarCoordenadas(location.getLatitude(), location.getLongitude(), 1);

                double ditancia_faltante = calcularDistancia(location.getLatitude(), location.getLongitude(), lat_destino, lng_destino);
                if(ditancia_faltante < 0.150){
                    Intent intent = new Intent().setClass(DestinoRonda.this, Supervision.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("supervision", supervision);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };


        int permiso = ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION );
        if(permiso == PackageManager.PERMISSION_DENIED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale( this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions( this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION}, 1 );
            }
        }
        locationManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, 0, 0, locationListener );


    }

    private static double calcularDistancia(double lat1, double lng1, double lat2, double lng2) {
        lat1 = Math.toRadians( lat1 );
        lat2 = Math.toRadians( lat2 );
        lng1 = Math.toRadians( lng1 );
        lng2 = Math.toRadians( lng2 );

        final double RADIO_TIERRA = 6371.01; // kilómetros
        double distancia = RADIO_TIERRA * Math.acos( Math.sin( lat1 ) * Math.sin( lat2 )
                + Math.cos(lat1) * Math.cos( lat2 ) * Math.cos( lng1 - lng2 ));
        return distancia;
    }

    private void getLastLocation() {

    }
}