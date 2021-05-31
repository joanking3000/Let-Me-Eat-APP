package com.example.letmeeat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class DetallesRestauranteUsuario extends AppCompatActivity {

    TextView titulo;
    Button claimLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();//Esconderemos la barra de accion que sale por defecto en la parte superior

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_restaurante_usuario);

        //Recolectamos la informacion en sus respectivas variables
        titulo  = findViewById(R.id.tv_Titulo);
        claimLocal = findViewById(R.id.reconocer_local);



        //Cogeremos los datos pasados
        String tituloPasado = getIntent().getStringExtra("titulo");
        titulo.setText(tituloPasado); //Lo mostramos en pantalla
    }
}