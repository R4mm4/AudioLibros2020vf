package net.ivanvega.audiolibros2020.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import net.ivanvega.audiolibros2020.MainActivity;
import net.ivanvega.audiolibros2020.R;

import java.io.IOException;
import java.util.Random;

public class MiServicio extends Service implements MediaPlayer.OnPreparedListener {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    @Override
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
    boolean libroEnCurso = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this,"Servicio Iniciado",Toast.LENGTH_SHORT).show();

            libroEnCurso = true;
            String input = intent.getStringExtra("inputExtra");
            String bookName = intent.getStringExtra("bookName");
            obtenerDireccion = Uri.parse(input);
            createNotificationChannel();
            Intent notificationIntent = new Intent(MiServicio.this, MainActivity.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    MiServicio.this,
                    0,
                    notificationIntent,
                    0
            );

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("MiAudioLibros")
                    .setContentText("Libro en reproducción: " + bookName)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(pendingIntent)
                    .build();

            startForeground(1, notification);

        try {
            StartAudio();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        StopAudio();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private final IBinder binder = new MiBinder();

    public MiServicio() {
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //mediaPlayer.start();
        mp.start();
    }

    public class MiBinder extends Binder {
        public MiServicio getService() {
            return MiServicio.this;
        }
    }

    public IBinder getBinder() {
        return binder;
    }


    private static String TAG = "ForegroundService";
    public static MediaPlayer mediaPlayer;

    public void StartAudio() throws IOException {
        Log.d("MENSAJEIMPORTANTE", "Comenzó a reproducirce audio");

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        try {
            mediaPlayer.setDataSource(getApplicationContext(), obtenerDireccion); // Establece la fuente del audio.
            mediaPlayer.prepareAsync(); //Prepara el archivo de la fuente.
        } catch (IOException e) {
            Log.e("Audiolibros", "ERROR: No se puede reproducir el audio.");
        }
    }

    public void StopAudio() {
        Log.d("MENSAJEIMPORTANTE", "Se detuvo la reproducción de audio");
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    /*public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        this.stopSelf();
    }*/
    private final Random ran = new Random();
    public int getRandomNumber() {
        return ran.nextInt(100);
    }
}
