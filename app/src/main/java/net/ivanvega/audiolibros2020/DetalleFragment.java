package net.ivanvega.audiolibros2020;

import android.app.ActivityManager;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;


import net.ivanvega.audiolibros2020.services.MiIntentService;
import net.ivanvega.audiolibros2020.services.MiServicio;
import net.ivanvega.audiolibros2020.services.MyBroadcastReceiver;

import java.io.IOException;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.

 * create an instance of this fragment.
 */
public class DetalleFragment extends Fragment implements View.OnTouchListener,MediaPlayer.OnPreparedListener,
        MediaController.MediaPlayerControl {

    public static String ARG_ID_LIBRO = "id_libro";
    MediaController mediaController;
    public void ponInfoLibro(int id) {
        ponInfoLibro(id, getView());
    }
    private void ponInfoLibro(int id, View vista) {
        Libro libro = Libro.ejemploLibros().elementAt(id);
        ((TextView) vista.findViewById(R.id.titulo)).setText(libro.titulo);
        String guartarBookLibroName = libro.titulo;
        ((TextView) vista.findViewById(R.id.autor)).setText(libro.autor);
        ((ImageView) vista.findViewById(R.id.portada)).setImageResource(libro.recursoImagen);
        vista.setOnTouchListener(this);


        Uri audio = Uri.parse(libro.urlAudio); // Uri que maneja la localización de archivos.
        String enviarUri = String.valueOf(audio);

        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (preferencias.getBoolean("pref_autoreproducir", true)) {
            Intent serviceIntent = new Intent(getActivity().getApplicationContext(), MiServicio.class);
            serviceIntent.putExtra("inputExtra", enviarUri);
            serviceIntent.putExtra("bookName", guartarBookLibroName);
            ContextCompat.startForegroundService(getActivity(), serviceIntent);
        }


        if (servicioLibros.mediaPlayer != null){
            servicioLibros.mediaPlayer.release();
        }
        servicioLibros. mediaPlayer = new MediaPlayer();
        servicioLibros.mediaPlayer.setOnPreparedListener(this);
        mediaController = new MediaController(getActivity());
        try {
            servicioLibros.mediaPlayer.setDataSource(getActivity(), audio);
            servicioLibros. mediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.e("Audiolibros", "ERROR: No se puede reproducir "+audio,e);
        }
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_detalle,container, false);
        Bundle args = getArguments();
        if (args != null) {
            int position = args.getInt(ARG_ID_LIBRO);
            ponInfoLibro(position, vista);
        } else {
            ponInfoLibro(0, vista);
        }
        return vista;
    }

    MiServicio servicioLibros = new MiServicio();

    /*@Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.d("Audiolibros", "Entramos en onPrepared de MediaPlayer");
        mediaPlayer.start();
        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(getView().findViewById(R.id.fragment_detalle));
        //mediaController.setPadding(0, 0, 0,110);
        mediaController.setEnabled(true);
        mediaController.show();
    }*/

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        mediaController.show();
        return false;
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d("Audiolibros", "Entramos en onPrepared de MediaPlayer");
        servicioLibros.mediaPlayer.start();
        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(getView());
        mediaController.setEnabled(true);
        mediaController.show();
    }

    @Override
    public void start() {
        servicioLibros.mediaPlayer.start();

    }
MediaPlayer mediaPlayer;
    MiServicio  miServicio;
    @Override
    public void pause() {
        servicioLibros.mediaPlayer.pause();
    }

    @Override
    public int getDuration() {
        return servicioLibros.mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return servicioLibros.mediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int i) {
        servicioLibros.mediaPlayer.seekTo(i);
    }

    @Override
    public boolean isPlaying() {
        return servicioLibros.mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return servicioLibros.mediaPlayer.getAudioSessionId();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}

