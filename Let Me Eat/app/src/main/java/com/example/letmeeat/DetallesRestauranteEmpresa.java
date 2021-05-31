package com.example.letmeeat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.letmeeat.util.AdaptadorListaPlatos;
<<<<<<< Updated upstream
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
=======
import com.example.letmeeat.util.JsonParser;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
>>>>>>> Stashed changes

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
import java.util.Map;

import Clases.Platos;

public class DetallesRestauranteEmpresa extends AppCompatActivity {
    protected RecyclerView mRecyclerView;
    protected AdaptadorListaPlatos mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected ArrayList<Platos> alPlatos;

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
        String idNegocio = getIntent().getStringExtra("idNegocio");
        mRecyclerView = (RecyclerView) this.findViewById(R.id.rv_platos_carta);
        mLayoutManager = new LinearLayoutManager(this);
        alPlatos = new ArrayList<Platos>();

        String url = "https://maps.googleapis.com/maps/api/place/details/json"+
                "?place_id="+idNegocio+
                "&key="+getResources().getString(R.string.google_map_key);

        new PlaceLugar().execute(url);

        


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
                mapList = jsonParser.parseResult(object);
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