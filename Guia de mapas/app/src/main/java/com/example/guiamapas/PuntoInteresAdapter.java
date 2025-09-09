package com.example.guiamapas;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PuntoInteresAdapter extends RecyclerView.Adapter<PuntoInteresAdapter.PuntoInteresViewHolder> {

    private List<PuntoInteres> puntosInteres;

    // Constructor para inicializar la lista de puntos de interés
    public PuntoInteresAdapter(List<PuntoInteres> puntosInteres) {
        this.puntosInteres = puntosInteres;
    }

    @Override
    public PuntoInteresViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflamos el layout de un item de punto de interés
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_punto_interes, parent, false);
        return new PuntoInteresViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PuntoInteresViewHolder holder, int position) {
        // Obtenemos el punto de interés para la posición actual y mostramos los datos
        final PuntoInteres punto = puntosInteres.get(position);
        holder.nombreTextView.setText(punto.getNombre());
        holder.descripcionTextView.setText(punto.getDescripcion());

        // Agregamos el listener para navegar a la actividad de detalles
        holder.itemView.setOnClickListener(v -> navigateToDetailActivity(v, punto));
    }

    @Override
    public int getItemCount() {
        return puntosInteres.size();
    }

    // Método para crear el Intent y navegar a la actividad de detalle
    private void navigateToDetailActivity(View v, PuntoInteres punto) {
        Intent intent = new Intent(v.getContext(), PuntoInteresDetailActivity.class);
        intent.putExtra("nombre", punto.getNombre());
        intent.putExtra("descripcion", punto.getDescripcion());
        intent.putExtra("horario", punto.getHorario()); // O el horario real que tengas
        intent.putExtra("imagenId", punto.getImagenId()); // ID de la imagen que quieres mostrar
        intent.putExtra("latitud", punto.getLatitud());
        intent.putExtra("longitud", punto.getLongitud());
        v.getContext().startActivity(intent); // Iniciamos la actividad
    }


    // ViewHolder para cada item del RecyclerView
    public static class PuntoInteresViewHolder extends RecyclerView.ViewHolder {
        TextView nombreTextView;
        TextView descripcionTextView;

        public PuntoInteresViewHolder(View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.textNombre);
            descripcionTextView = itemView.findViewById(R.id.textDescripcion);
        }
    }
}
