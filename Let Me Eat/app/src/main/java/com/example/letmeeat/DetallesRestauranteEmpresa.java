package com.example.letmeeat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.letmeeat.util.AdaptadorListaPlatos;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Clases.Platos;

public class DetallesRestauranteEmpresa extends AppCompatActivity {
    protected RecyclerView mRecyclerView;
    protected AdaptadorListaPlatos mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    Button claimLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();//Esconderemos la barra de accion que sale por defecto en la parte superior

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_restaurante_empresa);

        //Recolectamos la informacion en sus respectivas variables
        claimLocal = findViewById(R.id.reconocer_local);

        //Procedemos a ponerle un OnClick
        claimLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                claimearLocal();
            }
        });
        mRecyclerView = (RecyclerView) this.findViewById(R.id.rv_platos_carta);
        mLayoutManager = new LinearLayoutManager(this);
        ArrayList<Platos> alPlatos = new ArrayList<Platos>();
        mAdapter = new AdaptadorListaPlatos(alPlatos);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


    }

    private void claimearLocal() {
        //Utilizaremos el Firestore del firebase para almacenar nuestra informacion
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> infoLocalClaimeado = new HashMap<>();

        //Añadimos la informacion al hashmap que sera subido a la base de datos
        infoLocalClaimeado.put("Nombre", nom);
        infoLocalClaimeado.put("Apellidos", apellidosString);
        infoLocalClaimeado.put("Email", correo);
        infoLocalClaimeado.put("Empresa", empresa);

        //Procedemos a añadirlo en la coleccion LocalesClaimeados
        db.collection("LocalesClaimeados").document()
                .set(infoLocalClaimeado).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText()
            }
        })
    }
}