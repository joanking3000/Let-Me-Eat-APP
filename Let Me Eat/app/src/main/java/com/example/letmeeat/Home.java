package com.example.letmeeat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home extends AppCompatActivity implements OnMapReadyCallback {

    //Asignamos variables
    private GoogleMap mapa;
    private FusedLocationProviderClient fusedLocationClient;
    private SupportMapFragment mapFragment;
    double latActual = 0, longActual = 0;
    Button butZona, logout;
    boolean empresa = false; //Este lo usaremos para saber si el usuario actual es una empresa o no

    String correoUsuarioActual = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    DocumentReference usuarioActual = FirebaseFirestore.getInstance().collection("Usuarios").document(correoUsuarioActual);

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();//Esconderemos la barra de accion que sale por defecto en la parte superior
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Utilizaremos el DocumentReference del firebase para recolectar la info del database
        DocumentReference  docRef = FirebaseFirestore.getInstance().collection("Usuarios").document(correoUsuarioActual);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult(); //Leeremos el documento que en este caso sera el del usuario actual
                    if (document.getBoolean("Empresa").equals(false)){ //Si el valor del campo Empresa es false indicara que no es una empresa
                        empresa = false;
                    } else if (document.getBoolean("Empresa").equals(true)){//Si el valor del campo Empresa es true indicara que es una empresa
                        empresa = true;
                    }
                }
            }
        });

        mAuth = FirebaseAuth.getInstance();

        //Recolectamos las ids de los componentes que tengamos
        butZona = findViewById(R.id.but_BuscarPorZona);
        logout = findViewById(R.id.but_LogOut);

        //Usaremos el fragmento en el .xml llamado mapa para mostrar el mapa
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);

        //Nos servira para el tema de localizaciones
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //Comprobamos el permiso
        if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            miLocalizacion(); //Mostramos nuestra ubicacion y hacemos zoom

        } else {
            ActivityCompat.requestPermissions(Home.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 44);
        }

        //Aqui determinamos el funcionamiento del boton de Desconectar usuario
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                desconectarUsuario();
            }
        });

    }

    private void desconectarUsuario() {
        Intent i = new Intent(Home.this, Login.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mapa = googleMap;
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.layout_mapa));

        mapa.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                String tituloMarcador =  marker.getTitle();
                String idNegocio =  (String)marker.getTag();
                if (empresa == true){
                    Intent i = new Intent(Home.this,DetallesRestauranteEmpresa.class);
                    i.putExtra("titulo",tituloMarcador);
                    i.putExtra("idNegocio",idNegocio);
                    startActivity(i);
                } else {
                    Intent i = new Intent(Home.this,DetallesRestauranteUsuario.class);
                    i.putExtra("titulo",tituloMarcador);
                    i.putExtra("idNegocio",idNegocio);
                    startActivity(i);
                }
            }
        });
    }

    //Con este metodo mostraremos en el mapa nuestra localizacion actual
    private void miLocalizacion() {

        @SuppressLint("MissingPermission")
        //Inicializamos un task para localizacion
        Task<Location> task = fusedLocationClient.getLastLocation();

        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            MarkerOptions opciones = new MarkerOptions().position(latLng).title("Aqui estoy");
                            //Zoom
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                            //Añadimos el marcador al mapa
                            googleMap.addMarker(opciones);

                            //Al darle al boton buscara en una zona alrededor todos los restaurantes y les añadira un marker
                            butZona.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    double radioUsuario = calcularEscala(
                                                googleMap.getCameraPosition().zoom,
                                                googleMap.getCameraPosition().target) *
                                            calcularRadio(mapFragment.getView().getMeasuredWidth(),
                                                mapFragment.getView().getMeasuredHeight())/2;
                                    //Al hacer click al boton hara lo siguiente
                                    //Inicializamos la URL
                                    String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" + //URL
                                            "?location=" + googleMap.getCameraPosition().target.latitude + "," + googleMap.getCameraPosition().target.longitude + //Localizacion con altitud y longitud
                                            "&radius=" +radioUsuario + //En un radio dependiendo del zoom
                                            "&types=restaurant" + //Unicamente los restaurantes
                                            "&sensor=true" + //sensor
                                            "&key=" + getResources().getString(R.string.google_map_key); //Clave del google map

                                    new PlaceLugar().execute(url);
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    //En caso de que el codigo sea 44 (el cual es solicitar permiso en nuestro caso)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44 ){
            if (grantResults.length > 0 &&grantResults[0] == PackageManager.PERMISSION_GRANTED){
                miLocalizacion();
            }
        }
    }


    private double calcularRadio(int ancho, int alto){
        return Math.sqrt(ancho*ancho + alto*alto);
    }

    private double calcularEscala(double zoom, LatLng pos){
        return 36543.03392 * Math.cos(pos.latitude * Math.PI / 180)/Math.pow(2, zoom);
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
            new ParserTask().execute(s);
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
        String linia = ""; //TODO: Es linea zorra
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
            //Limpiamos el mapa
            mapa.clear();
            for (int i=0; i<hashMaps.size(); i++){
                //Inicializamos el hashmap
                HashMap<String, String> hashMapList = hashMaps.get(i);
                //Cogemos la latitud
                double lat = Double.parseDouble(hashMapList.get("lat"));
                //Cogemos la longitud
                double lng = Double.parseDouble(hashMapList.get("lng"));
                //Cogemos el nombre
                String name = hashMapList.get("name");
                //Cogemos el ID
                String id = hashMapList.get("id");
                //Concatenamos la latitud con la longitud
                LatLng latLng = new LatLng(lat, lng);
                //Inicializamos las marker options
                MarkerOptions opciones = new MarkerOptions();
                //Ponemos la posicion de cada lugar
                opciones.position(latLng);
                //Ponemos su titulo
                opciones.title(name);

                //Añadimos el marcador en el mapa
                Marker marker = mapa.addMarker(opciones);
                //Ponemos su id
                marker.setTag(id);
            }

        }
    }
}