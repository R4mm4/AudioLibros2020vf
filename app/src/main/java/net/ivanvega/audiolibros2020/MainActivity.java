package net.ivanvega.audiolibros2020;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //AQUI REVISO SI LE MANDARON PARAMETROS A ESTA ACTIVIDAD
        //EN ESPECIAL REVISO SI LA ACTIVIDAD LA INVOCO LA NOTIFICACION LANZADA POR EL SERVICIO DE PRIMER PLANO
        //
        if(getIntent().getExtras()!=null){
            Log.d("MSPPN", getIntent().getExtras().getString("rep","no se encontro parametro"));
        }


        SelectorFragment selectorFragment
                = new SelectorFragment();

        if ( findViewById(R.id.contenedor_pequeno) != null    &&
                getSupportFragmentManager().findFragmentById(R.id.contenedor_pequeno) == null
        ){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.contenedor_pequeno,
                            selectorFragment).commit();
        }

    }

    public void mostrarDetalle(int id) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if(fragmentManager.findFragmentById(R.id.detalle_fragment)!=null){

            DetalleFragment fragment =
                    (DetalleFragment)
                            fragmentManager.findFragmentById(R.id.detalle_fragment);

            fragment.ponInfoLibro(id);


        }else{
            DetalleFragment detalleFragment =
                    new DetalleFragment();

            Bundle bundle = new Bundle();

            bundle.putInt(DetalleFragment.ARG_ID_LIBRO, id);

            detalleFragment.setArguments(bundle);

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentManager.beginTransaction().replace(R.id.contenedor_pequeno
                    , detalleFragment).addToBackStack(null).commit();

        }

    }
}