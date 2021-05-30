package com.example.letmeeat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class DetallesRestauranteUsuario extends AppCompatActivity {

    TextView titulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_restaurante_usuario);

        titulo  = findViewById(R.id.tv_Titulo);

        //Cogeremos los datos pasados
        String tituloPasado = getIntent().getStringExtra("titulo");
        titulo.setText(tituloPasado); //Lo mostramos en pantalla
    }
}