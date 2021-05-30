package com.example.letmeeat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();//Esconderemos la barra de accion que sale por defecto en la parte superior
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}