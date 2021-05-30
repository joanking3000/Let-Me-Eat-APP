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

            //ponemos todos los valores en el hashmap
            datalist.put("name", name);
            datalist.put("lat", latitud);
            datalist.put("lng", longitud);
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
}
