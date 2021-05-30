package com.example.letmeeat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.letmeeat.util.AdaptadorListaPlatos;

import java.util.ArrayList;

import Clases.Platos;

public class DetallesRestauranteEmpresa extends AppCompatActivity {
    protected RecyclerView mRecyclerView;
    protected AdaptadorListaPlatos mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_restaurante_empresa);
        mRecyclerView = (RecyclerView) this.findViewById(R.id.rv_platos_carta);
        mLayoutManager = new LinearLayoutManager(this);
        ArrayList<Platos> alPlatos = new ArrayList<Platos>();
        mAdapter = new AdaptadorListaPlatos(alPlatos);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }
}