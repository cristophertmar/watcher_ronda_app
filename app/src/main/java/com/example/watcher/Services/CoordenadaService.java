package com.example.watcher.Services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.watcher.DestinoRonda;
import com.example.watcher.Menu;
import com.example.watcher.Model.AlarmaNotificacion;
import com.example.watcher.Model.PoligonoVerify;
import com.example.watcher.MyApplication;
import com.example.watcher.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;

import java.util.concurrent.Executor;

import static com.example.watcher.Utils.Apis.URL_HUB;


public class CoordenadaService extends Service {

    HubConnection hubConnection, hubpoligono;
    Handler handler = new Handler();
    private final int TIEMPO = 5000;

    public void onCreate() {
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int idProcess) {

        hubConnection = HubConnectionBuilder.create( URL_HUB + "alarmahub" ).build();
        hubpoligono = HubConnectionBuilder.create( URL_HUB + "poligono" ).build();
        createNotificationChannel();

        //Crear hubconnection
        if(hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED){
            hubConnection.start();
        }

        if(hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED){
            hubpoligono.start();
        }

        test();

        hubConnection.on( "recibirAlarma", (alarma) -> {
            if(alarma.getId_usuario() == ((MyApplication) this.getApplication()).getUsuario().getId()) {
                createNotification(alarma);
                //Menu.obtenerAlarma();
            }
        }, AlarmaNotificacion.class );


        /*new CountDownTimer(6000, 1000) {
            public void onTick(long millisUntilFinished) {
                Toast.makeText(getApplicationContext(), "seconds remaining: "  + millisUntilFinished / 1000 , Toast.LENGTH_SHORT).show();
            }
            public void onFinish() {
                createNotificationChannel();
                createNotification();
                Toast.makeText(getApplicationContext(), "Finalizado!" , Toast.LENGTH_SHORT).show();
            }


        }.start();*/
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if(hubConnection.getConnectionState() == HubConnectionState.CONNECTED){
            hubConnection.stop();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void test() {
        handler.postDelayed(new Runnable() {
            public void run() {
                // función a ejecutar
                //Toast.makeText(getApplicationContext(), "Este es un test", Toast.LENGTH_SHORT).show();

                handler.postDelayed(this, TIEMPO);
            }

        }, TIEMPO);
    }


    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Noticacion";
            NotificationChannel notificationChannel = new NotificationChannel("NOTIFICACION", name, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void createNotification(AlarmaNotificacion alarma){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "NOTIFICACION");
        builder.setSmallIcon(R.drawable.ic_baseline_report_24);
        builder.setContentTitle(alarma.getTitulo());
        builder.setContentText(alarma.getDescripcion());
        builder.setColor( Color.BLUE);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setLights(Color.MAGENTA, 1000, 1000);
        builder.setVibrate(new long[]{1000,1000,1000,1000,1000});
        builder.setDefaults( Notification.DEFAULT_SOUND);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(0, builder.build());
    }



}
