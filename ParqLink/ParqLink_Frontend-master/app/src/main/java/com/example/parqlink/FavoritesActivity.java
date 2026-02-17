package com.example.parqlink;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parqlink.DTO.ParkingResponse;

import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewFavorites;
    private ParkingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.items_listafavorites);

        recyclerViewFavorites = findViewById(R.id.recyclerViewFavorites);
        recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(this));

        List<ParkingResponse> favoriteList = FavoritesManager.getFavorites(); // lista estática por ahora
        adapter = new ParkingAdapter(favoriteList, true, parking -> {
            Toast.makeText(FavoritesActivity.this, "Seleccionado: " + parking.getName(), Toast.LENGTH_SHORT).show();
        });
        recyclerViewFavorites.setAdapter(adapter);
    }
}
