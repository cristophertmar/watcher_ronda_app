package com.example.watcher.Services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class LocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = "LocationService";
    private boolean currentlyProcessingLocation = false;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private String URL_GPS;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            //Log.e(TAG, "position: " + location.getLatitude() + ", " + location.getLongitude() + " accuracy: " + location.getAccuracy());

            // we have our desired accuracy of 500 meters so lets quit this service,
            // onDestroy will be called and stop our location uodates
            //if (location.getAccuracy() < 500.0f) {
            //    stopLocationUpdates();
            sendLocationDataToWebsite(location);
            //}
        }
    }

    protected void sendLocationDataToWebsite(Location location) {
        String ubicaciongps = location.getLongitude() + "*" + location.getLatitude();
        //float velocidad = location.getSpeed();
        Toast.makeText(getApplicationContext(), ubicaciongps , Toast.LENGTH_SHORT).show();
    }

    /**
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     * <p>
     * Llamado por los servicios de ubicación cuando la solicitud para conectar
     * al cliente finaliza correctamente. En este punto, puede solicitar la ubicación
     * actual o iniciar actualizaciones periódicas
     */
    @Override
    public void onConnected(Bundle bundle) {
        Log.e(TAG, "onConnected");

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000); // milliseconds
        locationRequest.setFastestInterval(5000); // the fastest rate in milliseconds at which your app can handle location updates
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } catch (SecurityException se) {
            //Vaya a la configuración y encuentre la aplicación Gps Tracker y habilite la ubicación
            Log.e(TAG, "Go into settings and find Gps Tracker app and enable Location.");
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed");
        //stopLocationUpdates();
        //stopSelf();
    }

    private void stopLocationUpdates() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "GoogleApiClient connection has been suspended.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // if we are currently trying to get a location and the alarm manager has called this again,
        // no need to start processing a new location.
        //Si actualmente estamos intentando obtener una ubicación y el administrador de alarmas lo ha llamado nuevamente,
        // no es necesario comenzar a procesar una nueva ubicación.

        Toast.makeText(getApplicationContext(), "Esta es una prueba" , Toast.LENGTH_SHORT).show();

        if (!currentlyProcessingLocation) {
            currentlyProcessingLocation = true;
            startTracking();
        }
        return START_NOT_STICKY;
    }

    private void startTracking() {
        //Log.d(TAG, "startTracking");

        // if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if (!googleApiClient.isConnected() || !googleApiClient.isConnecting()) {
            googleApiClient.connect();
        }
        //} else {
        //    Log.e(TAG, "unable to connect to google play services.");
        //}
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        //return null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true);
        }
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        //return super.onUnbind(intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(13, new Notification());
            return true; // Ensures onRebind() is called when a client re-binds.
        }
        return false;
    }

    @Override
    public void onRebind(Intent intent) {
        //super.onRebind(intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true);
        }
        super.onRebind(intent);
    }

    public class LocalBinder extends Binder {
        public LocationService getServerInstance() {
            return LocationService.this;
        }
    }

    /*private void envioGps(String ubicacionGPS, String imei, Float velocidad) {
        //if(ubicacionGPS != null){
        //Log.e("Precision", accuracy);
        int id;
        String ymdhms = StringUtils.yyyyMMddHHmmSS();
        URL_GPS = Constantes.HTTP_CABECERA + Constantes.URL_MONITOREAMIENTO_GPS + "sP=" + imei + ymdhms + ubicacionGPS;

        String estado = velocidad == 0.0 ? Constantes.ESTADO_ERROR_ENVIADO: Constantes.ESTADO_REGISTRO_URL_NO_ENVIADO;
        GpsQuery gps = new GpsQuery(this);
        id = gps.registrarGps(URL_GPS,estado);

        //Verificamos si haya red wifi o si esta usando datos.
        if(!Utils.redOrWifiActivo(this)) return;

        //Si tiene wifi o datos enviamos el gps. Ademas velocidad debe ser diferente de 0.0
        if(velocidad != 0.0) {
            EnvioUrlGps urlGps = new EnvioUrlGps();
            urlGps.execute(String.valueOf(id), "");
        }
        //}
    }*/

    private class EnvioUrlGps extends AsyncTask<String, Void, Void> {
        private HttpUrl.Builder httpBuider;
        private Request request;
        private OkHttpClient httpClient;
        private okhttp3.Response response = null;
        private String result;
        private String idOcurrencia;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //panelWait.setVisibility(View.VISIBLE);
            result = "";
            //if(ubicacionGPS!=null) {
            httpBuider = HttpUrl.parse(URL_GPS).newBuilder();
            httpClient = new OkHttpClient();
            //}
        }

        @Override
        protected Void doInBackground(String... params) {
            //httpBuider.addQueryParameter("pw", params[1]);
            //if(ubicacionGPS!=null) {
            idOcurrencia = params[0];
            request = new Request.Builder().url(httpBuider.build()).build();
            try {
                response = httpClient.newCall(request).execute();
                result = response.body().string();
            } catch (IOException e) {
                Log.e("ERROR USER", Log.getStackTraceString(e));
            }
            //}
            return null;
        }

        /*@Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //panelWait.setVisibility(View.GONE);
            //
            //if(ubicacionGPS!=null){
            try {
                GpsQuery gps = new GpsQuery(getApplicationContext());
                if (!StringUtils.cadenaEsVacia(result)) {
                    JSONObject obj = new JSONObject(result);
                    String valorRes = obj.getString(Constantes.RESULTADO);
                    //Toast.makeText(ControlPerdidasPrincipal.this, "url: " + URL_GPS, Toast.LENGTH_LONG).show();
                    if (!StringUtils.cadenaEsVacia(valorRes)) {
                        if (valorRes.equals("OK")) {
                            gps.updateGps(idOcurrencia, Constantes.ESTADO_REGISTRO_URL_ENVIADO);
                            Log.e("Enviado idGps", "" + idOcurrencia);
                            //Toast.makeText(ControlPerdidasPrincipal.this, "OK: ", Toast.LENGTH_LONG).show();
                        } else if (valorRes.equals("ERROR")) {
                            gps.updateGps(idOcurrencia, Constantes.ESTADO_REGISTRO_URL_CONTIENE_ERROR);
                            Log.e("NoSend error idGps", "" + idOcurrencia);
                            //Toast.makeText(ControlPerdidasPrincipal.this, "ERROR: ", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    gps.updateGps(idOcurrencia, Constantes.ESTADO_REGISTRO_URL_NO_ENVIADO);
                    Log.e("NoSend idGps", "" + idOcurrencia);
                    //Toast.makeText(ControlPerdidasPrincipal.this, "NO SE HA PODIDO ESTABLECER LA CONNEXIÓN.", Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            //}
        }*/
    }


}
