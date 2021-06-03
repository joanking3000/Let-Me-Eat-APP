package com.example.letmeeat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.letmeeat.util.AdaptadorListaPlatos;
import com.example.letmeeat.util.JsonParser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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

import Clases.Platos;

public class DetallesRestauranteUsuario extends AppCompatActivity {

    //Declaramos las variables para el recycle view
    protected RecyclerView mRecyclerView;
    protected AdaptadorListaPlatos mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected ArrayList<Platos> alPlatos = new ArrayList<Platos>();

    String idNegocio;
    Button obtenerPlatos;
    boolean duenio = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();//Esconderemos la barra de accion que sale por defecto en la parte superior

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_restaurante_usuario);

        //Iniciamos el recycleview
        mRecyclerView = (RecyclerView) this.findViewById(R.id.rv_platos_carta_usuario);
        mAdapter = new AdaptadorListaPlatos(alPlatos, mRecyclerView, duenio);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter(mAdapter);

        //Asignamos las variables con sus componentes
        obtenerPlatos = findViewById(R.id.but_obtenerPlatos_usuario);


        //Recolectamos la informacion pasada desde el otro activity
        idNegocio = getIntent().getStringExtra("idNegocio");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("LocalesClaimeados").document(idNegocio);

        String url = "https://maps.googleapis.com/maps/api/place/details/json" +
                "?place_id=" + idNegocio +
                "&key=" + getResources().getString(R.string.google_map_key);

        new PlaceLugar().execute(url);

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


    private void consultarPlatos() {
        alPlatos.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference resultadoPlatos = db.collection("LocalesClaimeados").document(idNegocio).collection("Platos");
        resultadoPlatos.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        alPlatos.add(new Platos(document.getId(), document.getString("Nombre"), document.getString("Detalles"), document.getDouble("Precio"), idNegocio));
                    }

                }
            }
        });
    }

    private class PlaceLugar extends AsyncTask<String, Integer, String> {
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
            new DetallesRestauranteUsuario.ParserTask().execute(s);
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
        while ((linia = br.readLine()) != null) {
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