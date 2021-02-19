package net.ivanvega.audiolibros2020.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.MediaController;

import androidx.core.app.NotificationCompat;

import net.ivanvega.audiolibros2020.DetalleFragment;
import net.ivanvega.audiolibros2020.MainActivity;
import net.ivanvega.audiolibros2020.R;

import java.io.IOException;

public class MyBroadcastReceiver <mediaController> extends MiServicio {
    MediaPlayer mediaPlayer;
    DetalleFragment detalleFragment = new DetalleFragment();
    MediaController mediaController;

    private final IBinder binder = new MiServicio.MiServicioBinder();
    public IBinder onBind(Intent intent){
        return binder;
    }
    public MyBroadcastReceiver(){

    }
    public class MiBinder extends Binder {

        public MyBroadcastReceiver getService() {
            return MyBroadcastReceiver.this;
        }

    }
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    public void onCreate() {
        super.onCreate();
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
    Uri obtenerDireccion;
    boolean actuallibro = false;
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (actuallibro == false) {
            actuallibro = true;
            String input = intent.getStringExtra("inputExtra");
            obtenerDireccion = Uri.parse(input);
            createNotificationChannel();
            Intent notificationIntent = new Intent(MyBroadcastReceiver.this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    MyBroadcastReceiver.this,
                    0,
                    notificationIntent,
                    0
            );
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Mi Audio Libros P77B")
                    .setContentText("Este es un servicio en primer plano")
                    .setSmallIcon(R.drawable.preview)
                    .setContentIntent(pendingIntent)
                    .build();

            startForeground(1, notification);

            try {
                StartAudio(mediaController);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return Service.START_STICKY;
        } else {
            StopAudio();
            String input = intent.getStringExtra("inputExtra");
            String input2 = intent.getStringExtra("in");
            obtenerDireccion = Uri.parse(input);
            createNotificationChannel();
            Intent notificationIntent = new Intent(MyBroadcastReceiver.this, MainActivity.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    MyBroadcastReceiver.this,
                    0,
                    notificationIntent,
                    0
            );

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Mi Audio Libros P77B")
                    .setContentText("Libro en reproducción: " + actuallibro)
                    .setSmallIcon(R.drawable.preview)
                    .setContentIntent(pendingIntent)
                    .build();

            startForeground(1, notification);
            try {
                StartAudio(mediaController);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return START_STICKY;
        }
    }
    @Override
    public void onDestroy() {
        mediaPlayer.stop();
        mediaPlayer.release();
        stopSelf();
        super.onDestroy();
    }
    public IBinder getBinder() {
        return binder;
    }
    private static String TAG = "ForegroundService";
    private boolean currentlySendingtAudio = false;

    public void StartAudio(MediaController mediaController) throws IOException {
        Log.d("MENSAJEIMPORTANTE", "Comenzó a reproducirce audio");
        currentlySendingtAudio = true;

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(getApplicationContext(), obtenerDireccion);
        mediaPlayer.prepare();
        mediaPlayer.setLooping(false);
        mediaPlayer.start();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

            }
        });
    }
    public void StopAudio() {
        Log.d("MENSAJEIMPORTANTE", "Se detuvo la reproducción de audio");
        currentlySendingtAudio = false;
        mediaPlayer.stop();
        mediaPlayer.release();
    }
}
