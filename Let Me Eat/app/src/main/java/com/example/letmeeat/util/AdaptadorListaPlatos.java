package com.example.letmeeat.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.letmeeat.DetallesRestauranteEmpresa;
import com.example.letmeeat.EditarDetallesPlato;
import com.example.letmeeat.R;

import java.util.ArrayList;

import Clases.Platos;

public class AdaptadorListaPlatos extends RecyclerView.Adapter<AdaptadorListaPlatos.ViewHolder> {

    static ArrayList<Platos> lPlatos;
    static String idPlato, nombreplato, detallesplato, idNegocio;
    static double precioplato;
    static RecyclerView mRecyclerView;
    static String userActual;

    public AdaptadorListaPlatos(ArrayList<Platos>lPlatos, RecyclerView mRecyclerView, String userActual) {
        this.lPlatos = lPlatos;
        this.mRecyclerView = mRecyclerView;
        this.userActual = userActual;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fila_plato_carta, parent, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvNombrePlato.setText(lPlatos.get(position).getNombre());
        holder.tvDetalles.setText("Detalles: " +lPlatos.get(position).getDetalles());
        holder.tvPrecio.setText( "Precio: " + String.valueOf(lPlatos.get(position).getPrecio()) + "€") ;
    }

    @Override
    public int getItemCount() {
        return lPlatos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNombrePlato, tvDetalles, tvPrecio;

        public ViewHolder(View v) {
            super(v);
            Context context = v.getContext();

            //Con esto podremos saber el nombre de la clase que accede
            String nombreActividadEntrante = ((Activity) context).getLocalClassName();

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int posicion = mRecyclerView.getChildLayoutPosition(v);

                        idPlato = lPlatos.get(posicion).getId();
                        nombreplato = lPlatos.get(posicion).getNombre();
                        detallesplato = lPlatos.get(posicion).getDetalles();
                        precioplato = lPlatos.get(posicion).getPrecio();
                        idNegocio = lPlatos.get(posicion).getIdNegocio();

                        if (nombreActividadEntrante.equals("DetallesRestauranteEmpresa")){
                        Intent i = new Intent(context, EditarDetallesPlato.class);
                        i.putExtra("idPlato", idPlato);
                        i.putExtra("nombreplato", nombreplato);
                        i.putExtra("detallesplato", detallesplato);
                        i.putExtra("precioplato", precioplato);
                        i.putExtra("idNegocio", idNegocio);
                        i.putExtra("userActual", userActual);

                        context.startActivity(i);
                        }

                    }
                });


            //TODO: Esto se usa en algun lugar al final?
            tvNombrePlato = (TextView) v.findViewById(R.id.tv_NombrePlato);
            tvDetalles = (TextView) v.findViewById(R.id.tv_EsVegano);
            tvPrecio = (TextView) v.findViewById(R.id.tv_Detalles);
        }

    }

}
