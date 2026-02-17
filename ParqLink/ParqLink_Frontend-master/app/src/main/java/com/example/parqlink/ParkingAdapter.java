package com.example.parqlink;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parqlink.DTO.ParkingResponse;

import java.util.List;

public class ParkingAdapter extends RecyclerView.Adapter<ParkingAdapter.ParkingViewHolder> {

    private final List<ParkingResponse> parkingList;
    private final boolean enModoFavoritos;
    private final OnParkingClickListener clickListener;

    public ParkingAdapter(List<ParkingResponse> parkingList, boolean enModoFavoritos, OnParkingClickListener clickListener) {
        this.parkingList = parkingList;
        this.enModoFavoritos = enModoFavoritos;
        this.clickListener = clickListener;
    }


    @NonNull
    @Override
    public ParkingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.items_parking, parent, false);
        return new ParkingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParkingViewHolder holder, int position) {
        ParkingResponse parking = parkingList.get(position);
        holder.name.setText(parking.getName());
        holder.address.setText(parking.getAddress());
        holder.price.setText("Tarifa: " + parking.getPricePerHour() + "€/hora");
        Double distanceKm = parking.getDistance();
        if (distanceKm != null) {
            String formatted = String.format("Distancia: %.2f km", distanceKm);
            holder.distance.setText(formatted);
        } else {
            holder.distance.setText("Distancia: N/D");
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onParkingClick(parking);
            }
        });

        holder.btnMenuOptions.setOnClickListener(v -> {
            Context wrapper = new ContextThemeWrapper(v.getContext(), R.style.WhitePopupMenu);
            PopupMenu popupMenu = new PopupMenu(wrapper, holder.btnMenuOptions);
            popupMenu.getMenu().add(
                    enModoFavoritos ? "Quitar de favoritos" : "Guardar en favoritos"
            ).setOnMenuItemClickListener(item -> {
                int pos = holder.getAdapterPosition();

                if (enModoFavoritos) {
                    FavoritesManager.removeFavorite(parking, v.getContext());
                    parkingList.remove(pos);
                    notifyItemRemoved(pos);
                    notifyItemRangeChanged(pos, parkingList.size());
                    Toast.makeText(v.getContext(), "Eliminado de favoritos: " + parking.getName(), Toast.LENGTH_SHORT).show();
                } else {
                    FavoritesManager.addFavorite(parking, v.getContext());
                    Toast.makeText(v.getContext(), "Guardado en favoritos: " + parking.getName(), Toast.LENGTH_SHORT).show();
                }
                return true;
            });

            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return parkingList.size();
    }

    static class ParkingViewHolder extends RecyclerView.ViewHolder {
        TextView name, address, price, distance;
        ImageView btnMenuOptions;

        public ParkingViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.parkingName);
            address = itemView.findViewById(R.id.parkingAddress);
            price = itemView.findViewById(R.id.parkingPrice);
            distance = itemView.findViewById(R.id.parkingDistance);
            btnMenuOptions = itemView.findViewById(R.id.btnMenuOptions);
        }
    }
}

