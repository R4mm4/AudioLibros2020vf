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

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.IBinder;
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
 * Use the {@link DetalleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetalleFragment extends Fragment
                            implements View.OnTouchListener,
                            MediaPlayer.OnPreparedListener,
        MediaController.MediaPlayerControl
{
    MiServicio  miServicio;
    public static String ARG_ID_LIBRO = "id_libro";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    boolean mBound = false;
    boolean guardarlibro = false;
    boolean seGuardo= false;
    MediaPlayer mediaPlayer;
    MediaController mediaController;
    Intent iSer;
    MyBroadcastReceiver myBroadcastReceiver;


    public DetalleFragment() {
        // Required empty public constructor
    }
    /*private boolean isMyServiceRunning(String serviceClass){
        final ActivityManager detalleFragment = (ActivityManager) Application.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = detalleFragment.getRunningServices(Integer.MAX_VALUE);
        for(ActivityManager.RunningServiceInfo runningServiceInfo : services){
            if(runningServiceInfo.service.getClassName().equals(serviceClass))
                return true;
        }
        return false;
    }*/

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetalleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetalleFragment newInstance(String param1, String param2) {
        DetalleFragment fragment = new DetalleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

       View vista = inflater.inflate(R.layout.fragment_detalle, container, false);

        Bundle args = getArguments();

        if (args != null) {
            int position = args.getInt(ARG_ID_LIBRO);
            ponInfoLibro(position, vista);
        } else {
            ponInfoLibro(0, vista);
        }



        return vista;
    }

    public void ponInfoLibro(int id) {
        ponInfoLibro(id, getView());
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MiServicio.MiServicioBinder miServicioBinder =
                    (MiServicio.MiServicioBinder) iBinder ;
             miServicio =
                    miServicioBinder.getService();

            Log.d("MSE", "GFrameno enlazado al seervicio " + componentName);

             int randf =  miServicio.getRandomNumber();
            Log.d("MSE", "Peticion  al servicio " + randf);

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    private void ponInfoLibro(int id, View vista) {

        //servicio iniciado
        //servicio de primer plano
        iSer = new Intent(getContext(), MiServicio.class);
        getActivity().startService(iSer);

        getActivity().bindService(iSer, serviceConnection, Context.BIND_AUTO_CREATE);

        Intent miIS = new Intent(getContext(), MiIntentService.class);
        getActivity().startService(miIS);



        Libro libro =
                Libro.ejemploLibros().elementAt(id);
        ((TextView) vista.findViewById(R.id.titulo)).setText(libro.titulo);
        ((TextView) vista.findViewById(R.id.autor)).setText(libro.autor);
        ((ImageView) vista.findViewById(R.id.portada)).setImageResource(libro.recursoImagen);

        vista.setOnTouchListener(this);

        if (mediaPlayer != null){
            mediaPlayer.release();
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        mediaController = new MediaController(getActivity());
        Uri audio = Uri.parse(libro.urlAudio);
        try {
            mediaPlayer.setDataSource(getActivity(), audio);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.e("Audiolibros", "ERROR: No se puede reproducir "+audio,e);
        }





    }

    @Override
    public void onStop() {
        super.onStop();

        mediaController.hide();
        try {
            // mediaPlayer.stop();
            // mediaPlayer.release();

        } catch (Exception e) {
            Log.d("Audiolibros", "Error en mediaPlayer.stop()");
            super.onStop();
        }

    }

        @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        mediaController.show();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.d("Audiolibros", "Entramos en onPrepared de MediaPlayer");
        mediaPlayer.start();
        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(getView());
        mediaController.setEnabled(true);
        mediaController.show();
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (preferencias.getBoolean("pref_autoreproducir", true)) {
            Intent serviceIntent = new Intent(getActivity().getApplicationContext(), BroadcastReceiver.class);
            serviceIntent.putExtra("inputExtra", guardarlibro);
            serviceIntent.putExtra("in", seGuardo);
            ContextCompat.startForegroundService(getActivity(), serviceIntent);
            //bindService();
        }
    }

    @Override
    public void start() {
        mediaPlayer.start();
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
        myBroadcastReceiver.StopAudio();
        int randf =  miServicio.getRandomNumber();
        Log.d("MSE", "Peticion  al servicio " + randf);
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int i) {
        mediaPlayer.seekTo(i);
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
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
        return mediaPlayer.getAudioSessionId();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void onServiceConnected(ComponentName name, IBinder service) {
        MyBroadcastReceiver.MiBinder miBinder = (MyBroadcastReceiver.MiBinder) service;
        myBroadcastReceiver = miBinder.getService();
        mBound = true;
    }

    public void onServiceDisconnected(ComponentName name) {
        mBound = false;
    }
    void bindService() {
        getActivity().bindService(new Intent(getActivity(), MyBroadcastReceiver.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    void unBindService() {
        if (mBound == true) {
            getActivity().unbindService(serviceConnection);
            mBound = false;
        }
    }
}