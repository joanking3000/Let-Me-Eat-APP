package com.example.letmeeat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class AgregarPlatos extends AppCompatActivity {

    String idNegocio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_platos);

        //Recolectamos la informacion pasada desde el otro activity
        idNegocio = getIntent().getStringExtra("idNegocio");


    }
}