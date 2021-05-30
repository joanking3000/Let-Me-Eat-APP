package com.example.letmeeat.util;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.letmeeat.R;

import java.util.ArrayList;

import Clases.Platos;

public class AdaptadorListaPlatos extends RecyclerView.Adapter<AdaptadorListaPlatos.ViewHolder> {

    ArrayList<Platos> lPlatos;

    public AdaptadorListaPlatos(ArrayList<Platos>lPlatos) {
        this.lPlatos = lPlatos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fila_plato_carta, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AdaptadorListaPlatos.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return lPlatos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivPlato;
        private final TextView tvTitulo;
        private final TextView tvDescripcion;
        private final TextView tvAlgo;

        public ViewHolder(View v) {
            super(v);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("PRUEBA CLICKI", "SOLO COMPRUENA UN CLICKI");
                }
            });

            ivPlato = (ImageView) v.findViewById(R.id.imageView4);
            tvTitulo = (TextView) v.findViewById(R.id.textView4);
            tvDescripcion = (TextView) v.findViewById(R.id.textView5);
            tvAlgo = (TextView) v.findViewById(R.id.textView6);
        }
    }

}
