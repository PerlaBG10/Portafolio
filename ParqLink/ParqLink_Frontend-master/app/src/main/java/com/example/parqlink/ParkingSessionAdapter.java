package com.example.parqlink;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parqlink.DTO.ParkingSessionResponse;

import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ParkingSessionAdapter extends RecyclerView.Adapter<ParkingSessionAdapter.ViewHolder> {

    private List<ParkingSessionResponse> sessionList;

    public ParkingSessionAdapter(List<ParkingSessionResponse> sessionList) {
        this.sessionList = sessionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_parking_session, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ParkingSessionResponse session = sessionList.get(position);

        DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("HH:mm:ss , yyyy-MM-dd");

        LocalDateTime start = LocalDateTime.parse(session.getStartTime(), inputFormatter);
        holder.startTime.setText("Inicio: " + start.format(outputFormatter));
        holder.parkingName.setText(session.getParkingName());
        if (session.getEndTime() != null) {
            LocalDateTime end = LocalDateTime.parse(session.getEndTime(), inputFormatter);
            holder.endTime.setText("Fin: " + end.format(outputFormatter));
        } else {
            holder.endTime.setText("Fin: Sesión activa");
        }
        if (session.getTotalCost() == null) {
            holder.totalCost.setText("Costo: En curso");
        }
    }

    @Override
    public int getItemCount() {
        return sessionList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView parkingName, startTime, endTime, totalCost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parkingName = itemView.findViewById(R.id.tvParkingName);
            startTime = itemView.findViewById(R.id.tvStartTime);
            endTime = itemView.findViewById(R.id.tvEndTime);
            totalCost = itemView.findViewById(R.id.parkingPrice);
        }
    }
}
