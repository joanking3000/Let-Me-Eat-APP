package com.example.letmeeat;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Carga extends AppCompatActivity {

    FirebaseUser usuario;
    private class CargaVista extends AsyncTask<Void, Void, Boolean> {
        private Activity actividad;

        public CargaVista(Activity a) {
            actividad = a;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean prueba = false;
            try {
                synchronized (this){
                    int contador = 0;

                    //Esperara 5 segundos hasta que la aplicacion cambie de actividad
                    while (contador < 5){
                        this.wait(1000);
                        contador++;
                    }
                }
                prueba = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return prueba;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (usuario != null) {
                //El usuario esta logado
                Intent intent = new Intent(actividad.getApplicationContext(), Home.class);
                actividad.startActivity(intent);
                finish();
            } else {
                //No hay ningun usuario logeado actualmente
                Intent intent = new Intent(actividad.getApplicationContext(), Login.class);
                actividad.startActivity(intent);
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().hide();//Esconderemos la barra de accion que sale por defecto en la parte superior
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carga);

        //Escondemos la barra de navegacion
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        new CargaVista(this).execute();
        usuario = FirebaseAuth.getInstance().getCurrentUser();
    }
}