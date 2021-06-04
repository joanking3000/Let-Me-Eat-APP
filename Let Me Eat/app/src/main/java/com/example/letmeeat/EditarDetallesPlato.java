package com.example.letmeeat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditarDetallesPlato extends AppCompatActivity {

    String idPlato, idNegocio;
    String nombreplatoold;
    String detallesplatoold, userActual;
    double precio;
    Button editarplato, eliminarplato;
    EditText nombreplato, detalles, precioet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();//Esconderemos la barra de accion que sale por defecto en la parte superior
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_detalles_plato);

        //Recolectamos la informacion pasada desde el otro activity
        idPlato = getIntent().getStringExtra("idPlato");
        nombreplatoold = getIntent().getStringExtra("nombreplato");
        detallesplatoold = getIntent().getStringExtra("detallesplato");
        precio = getIntent().getDoubleExtra("precioplato", 0);
        idNegocio = getIntent().getStringExtra("idNegocio");
        userActual = getIntent().getStringExtra("userActual");



        //Declaramos las variables
        editarplato = findViewById(R.id.but_editarplato);
        eliminarplato = findViewById(R.id.but_eliminarPlato);
        nombreplato = findViewById(R.id.et_nombreplatoedit);
        detalles = findViewById(R.id.et_detallesedit);
        precioet = findViewById(R.id.et_precioedit);

        //Ponemos en los campos los valores actuales del plato escogido
        nombreplato.setText(nombreplatoold);
        detalles.setText(detallesplatoold);
        precioet.setText((String.valueOf(precio)));

        //Abrimos la conexion con la base de datos
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> infoPlato = new HashMap<>();

        DocumentReference docref =  db.collection("LocalesClaimeados").document(idNegocio);
        docref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (!documentSnapshot.getString("Duenio").equals(userActual)){
                    Toast.makeText(EditarDetallesPlato.this, "El usuario actual no es dueño de este local, pongase en contacto con nosotros", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });

        //Ponemos el OnClick de editarplato
        editarplato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cogemos el valor de los campos
                String stringnombreplato = nombreplato.getText().toString().trim(),
                        stringdetalles = detalles.getText().toString().trim();
                Double doubleprecio = Double.parseDouble(precioet.getText().toString());

                //Añadimos la informacion al hashmap que sera subido a la base de datos
                infoPlato.put("Nombre", stringnombreplato);
                infoPlato.put("Detalles", stringdetalles);
                infoPlato.put("Precio", doubleprecio);

                //Updateamos el documento
                DocumentReference docref =  db.collection("LocalesClaimeados").document(idNegocio)
                        .collection("Platos").document(idPlato);
                docref.update(infoPlato).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditarDetallesPlato.this, "Plato editado satisfactoriamente", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            }
        });

        //Funcion del boton de borrar plato
        eliminarplato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Eliminamos el documento
                DocumentReference docref =  db.collection("LocalesClaimeados").document(idNegocio)
                        .collection("Platos").document(idPlato);
                docref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditarDetallesPlato.this, "Plato eliminado correctamente", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            }
        });
    }
}