package com.example.letmeeat.util;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonParser {
    private HashMap<String,String> parseJsonObject(JSONObject object){
        //Iniciamos el hashmap
        HashMap<String, String> datalist = new HashMap<>();
        try {
            //Cogemos el nombre del objeto
            String name =object.getString("name");

            //Cogemos la latitud del objecto
            String latitud = object.getJSONObject("geometry").getJSONObject("location").getString("lat");

            //Cogemos la longitud del objeto
            String longitud = object.getJSONObject("geometry").getJSONObject("location").getString("lng");

            //cogemos el ID del objeto Places
            String idPlaces =  object.getString("place_id");

            //ponemos todos los valores en el hashmap
            datalist.put("name", name);
            datalist.put("lat", latitud);
            datalist.put("lng", longitud);
            datalist.put("id", idPlaces);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return datalist;
    }

    private List<HashMap<String, String>> parseJsonArray(JSONArray jsonArray){
        //Inicializamos el hashmap
        List<HashMap<String, String>> datalist = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                HashMap<String, String> data = parseJsonObject((JSONObject) jsonArray.get(i));
                //añadimos el data al hashmaplist
                datalist.add(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return datalist;
    }

    public List<HashMap<String, String>> parseResult(JSONObject object){
        //Inicializamos el JSONArray
        JSONArray jsonArray = null;
        //Cogemos el resultado
        try {
            jsonArray = object.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Devolvemos el array
        return  parseJsonArray(jsonArray);

    }

    public List<HashMap<String, String>> parseResultDetalles(JSONObject object){
        //Inicializamos el JSONArray
        JSONObject jsonArray = null;
        //Cogemos el resultado
        try {
            jsonArray = object.getJSONObject("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Devolvemos el array
        return  parseJsonArrayDetalles(jsonArray);

    }

    private List<HashMap<String, String>> parseJsonArrayDetalles(JSONObject jsonArray){
        //Inicializamos el hashmap
        List<HashMap<String, String>> datalist = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            HashMap<String, String> data = parseJsonObjectDetalles(jsonArray);
            //añadimos el data al hashmaplist
            datalist.add(data);
        }
        return datalist;
    }

    private HashMap<String,String> parseJsonObjectDetalles(JSONObject object){
        //Iniciamos el hashmap
        HashMap<String, String> datalist = new HashMap<>();
        try {
            //Cogemos el nombre del objeto
            String name = object.getString("name");
            boolean localAbierto = object.getJSONObject("opening_hours").getBoolean("open_now");

            //ponemos todos los valores en el hashmap
            datalist.put("name", name);
            datalist.put("abierto", String.valueOf(localAbierto));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return datalist;
    }
}
