package com.example.watcher;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

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

public class DestinoRonda extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    AlertDialog alerta = null;
    HubConnection hubConnection;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient( this );

        subirLatLongHub();


        //Crear hubconnection
        hubConnection = HubConnectionBuilder.create( "http://192.168.0.4/watcher/coordenadahub" ).build();
        hubConnection.start();

        getAlertaNotGps();
        /*getLocation();*/
        setContentView( R.layout.activity_destino_ronda );
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById( R.id.map );
        mapFragment.getMapAsync( this );

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
        final AlertDialog.Builder builder = new AlertDialog.Builder( this );
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
        alerta.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng destino = new LatLng( -12.0908, -77.0839 );
        mMap.addMarker( new MarkerOptions().position( destino ).title( "Destino" ) );
        mMap.moveCamera( CameraUpdateFactory.newLatLng( destino ) );
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target( destino )
                .zoom( 12 )
                .bearing( 90 )
                .tilt( 45 )
                .build();
        mMap.animateCamera( CameraUpdateFactory.newCameraPosition( cameraPosition ) );

        if (ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            getLastLocation();
            return;
        }
        mMap.setMyLocationEnabled( true );
        mMap.getUiSettings().setMyLocationButtonEnabled( false );
        LocationManager locationManager = (LocationManager) DestinoRonda.this.getSystemService( Context.LOCATION_SERVICE );
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLng miUbicacion = new LatLng( location.getLatitude(), location.getLongitude() );

                Toast.makeText(getApplicationContext(), "Cambiando ubicación", Toast.LENGTH_SHORT).show();


                mMap.addMarker( new MarkerOptions().position( miUbicacion ).title( "Motorizado" ));
                if(hubConnection.getConnectionState() == HubConnectionState.CONNECTED){
                    hubConnection.send( "enviarCoordenada",  location.getLatitude(), location.getLongitude(), 1);
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

    private void getLastLocation() {

    }
}