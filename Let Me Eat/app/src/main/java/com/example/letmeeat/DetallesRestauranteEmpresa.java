package com.example.letmeeat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.letmeeat.util.AdaptadorListaPlatos;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.letmeeat.util.JsonParser;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Clases.Platos;

public class DetallesRestauranteEmpresa extends AppCompatActivity {
    //Declaramos las variables para el recycle view
    protected RecyclerView mRecyclerView;
    protected AdaptadorListaPlatos mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected ArrayList<Platos> alPlatos = new ArrayList<Platos>();

    Button claimLocal, agregarPlato, obtenerPlatos;
    String idNegocio;
    String correoUsuarioActual = FirebaseAuth.getInstance().getCurrentUser().getEmail(); //Cogemos el correo del usuario actual
    boolean duenio = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();//Esconderemos la barra de accion que sale por defecto en la parte superior
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_restaurante_empresa);


        mRecyclerView = (RecyclerView) this.findViewById(R.id.rv_platos_carta_usuario);
        mAdapter = new AdaptadorListaPlatos(alPlatos, mRecyclerView, correoUsuarioActual);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter(mAdapter);

        //Asignamos las variables con sus componentes
        claimLocal = findViewById(R.id.reconocer_local);
        agregarPlato = findViewById(R.id.agregar_plato);
        obtenerPlatos = findViewById(R.id.but_obtenerPlatos_usuario);

        //Recolectamos la informacion pasada desde el otro activity
        idNegocio = getIntent().getStringExtra("idNegocio");
        final boolean[] esClaimeado = {false};

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("LocalesClaimeados").document(idNegocio);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.getString("Duenio") != null && documentSnapshot.getString("Duenio").equals(correoUsuarioActual)){
                    duenio = true;
                    agregarPlato.setVisibility(View.VISIBLE);
                    esClaimeado[0] = true;
                    claimLocal.setVisibility(View.GONE);
                    obtenerPlatos.setVisibility(View.VISIBLE);
                } else if (!documentSnapshot.getString("Duenio").equals(correoUsuarioActual)){
                    agregarPlato.setVisibility(View.GONE);
                    esClaimeado[0] = true;
                    claimLocal.setVisibility(View.VISIBLE);
                    claimLocal.setEnabled(false);
                    claimLocal.setText("Local con dueño");
                    obtenerPlatos.setVisibility(View.VISIBLE);


                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                esClaimeado[0] = false;
                agregarPlato.setVisibility(View.GONE);
                claimLocal.setVisibility(View.VISIBLE);
                obtenerPlatos.setVisibility(View.GONE);
            }
        });

        if(esClaimeado[0]){
            claimLocal.setVisibility(View.GONE);
        } else {
            //Procedemos a ponerle un OnClick al boton de claimear
            claimLocal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    claimearLocal();
                }
            });
        }

        //TODO: Comentar esta seccion



        String url = "https://maps.googleapis.com/maps/api/place/details/json"+
                "?place_id="+idNegocio+
                "&key="+getResources().getString(R.string.google_map_key);

        new PlaceLugar().execute(url);





        //Le ponemos un OnClick a añadir plato
        agregarPlato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetallesRestauranteEmpresa.this,AgregarPlatos.class);
                i.putExtra("idNegocio",idNegocio);
                startActivity(i);
            }
        });

        obtenerPlatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        consultarPlatos();
        mAdapter.notifyDataSetChanged();

    }


    private void consultarPlatos(){
        alPlatos.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference resultadoPlatos = db.collection("LocalesClaimeados").document(idNegocio).collection("Platos");
        resultadoPlatos.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()){
                    for (DocumentSnapshot document :queryDocumentSnapshots.getDocuments()) {
                        alPlatos.add(new Platos(document.getId(), document.getString("Nombre"), document.getString("Detalles"), document.getDouble("Precio"), idNegocio));
                    }

                }
            }
        });
    }
    private void claimearLocal() {
        //Utilizaremos el Firestore del firebase para almacenar nuestra informacion
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        Map<String, Object> infoLocalClaimeado = new HashMap<>();

        //Añadimos la informacion al hashmap que sera subido a la base de datos
        infoLocalClaimeado.put("Duenio", correoUsuarioActual);



        //Procedemos a añadirlo en la coleccion LocalesClaimeados
        db.collection("LocalesClaimeados").document(idNegocio)
                .set(infoLocalClaimeado).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(DetallesRestauranteEmpresa.this, "Nos pondremos en contacto con la empresa para verificar la autenticidad", Toast.LENGTH_LONG).show();
                enviarCorreoParaClaimear();
                claimLocal.setVisibility(View.GONE);
                agregarPlato.setVisibility(View.VISIBLE);
            }
        });


    }





    private void enviarCorreoParaClaimear(){
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, "jjperdiguer@gmail.com");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Solicitud de claimeo para usuario [" + correoUsuarioActual + "]");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "El usuario [" + correoUsuarioActual + "] solicita claimear el negocio con id [" + idNegocio + "]");

        try {
            startActivity(Intent.createChooser(emailIntent, "Enviando email..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(DetallesRestauranteEmpresa.this, "No hay ninguna app de correo instalada.", Toast.LENGTH_SHORT).show();
        }
    }


    private class PlaceLugar extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... strings) {
            String data = null;
            try {
                data = descargarURL(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            new DetallesRestauranteEmpresa.ParserTask().execute(s);
        }
    }

    private String descargarURL(String string) throws IOException {
        URL url = new URL(string);
        //Inicializamos el conector
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //Conectamos
        connection.connect();
        //Inicializamos Input Stream
        InputStream stream = connection.getInputStream();
        //Inicializamos el buffer
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        //Inicializamos el StringBuilder
        StringBuilder builder = new StringBuilder();
        //Iniciamos la variable para el String
        String linia = "";
        while ((linia = br.readLine()) != null){
            builder.append(linia);
        }

        //Recoger los datos del append
        String data = builder.toString();
        //Cerramos el reader
        br.close();

        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {
        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
            //Creamos el parser de json
            JsonParser jsonParser = new JsonParser();
            //Inicializamos el hashmap list
            List<HashMap<String, String>> mapList = null;
            //Initializamos el objeto json
            JSONObject object = null;
            try {
                object = new JSONObject(strings[0]);
                mapList = jsonParser.parseResultDetalles(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return mapList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {

        }
    }


}