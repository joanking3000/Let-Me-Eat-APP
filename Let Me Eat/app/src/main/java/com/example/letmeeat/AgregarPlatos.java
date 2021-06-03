package com.example.letmeeat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AgregarPlatos extends AppCompatActivity {

    String idNegocio;
    Button agregarplato;
    EditText nombreplato, detalles, precioet;
    double precio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_platos);

        //Recolectamos la informacion pasada desde el otro activity
        idNegocio = getIntent().getStringExtra("idNegocio");

        //Declaramos las variables
        agregarplato = findViewById(R.id.but_aniadirplato);
        nombreplato = findViewById(R.id.et_nombreplato);
        detalles = findViewById(R.id.et_detalles);
        precioet = findViewById(R.id.et_precio);



        agregarplato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Pasamos los valores a string ignorando cualquier espacio en blanco que haya a principio o final
                String stringnombreplato = nombreplato.getText().toString().trim(),
                        stringdetalles = detalles.getText().toString().trim();

                //Cogemos el valor del precio
                precio = Double.parseDouble(precioet.getText().toString());

                //Control de errores
                if (stringnombreplato.isEmpty()){
                    nombreplato.setError("Se requiere un nombre para el plato");
                    nombreplato.requestFocus();
                    return;
                }
                if (stringdetalles.isEmpty()){
                    detalles.setError("Se requiere una minima descripción del plato");
                    detalles.requestFocus();
                    return;
                }


                //Utilizaremos el Firestore del firebase para almacenar nuestra informacion
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> infoPlato = new HashMap<>();

                //Añadimos la informacion al hashmap que sera subido a la base de datos
                infoPlato.put("Nombre", stringnombreplato);
                infoPlato.put("Detalles", stringdetalles);
                infoPlato.put("Precio", precio);

                //Insertamos el plato como una coleccion con idautogenerada
                db.collection("LocalesClaimeados").document(idNegocio)
                        .collection("Platos").document().set(infoPlato).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AgregarPlatos.this, "Plato añadido correctamente", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AgregarPlatos.this, "Ha sucedido un error al añadir el plato, intentelo mas tarde", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            }
        });
    }
}