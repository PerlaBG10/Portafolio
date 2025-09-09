package com.example.guiamapas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CategoriaAdapter adapter;
    private List<String> categorias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializamos el RecyclerView
        recyclerView = findViewById(R.id.recyclerViewCategorias);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Lista de categorías
        categorias = new ArrayList<>();
        categorias.add("Restaurantes");
        categorias.add("Museos");
        categorias.add("Parques");
        categorias.add("Monumentos");
        categorias.add("Teatros");

        // Adaptador para el RecyclerView
        adapter = new CategoriaAdapter(categorias, categoria -> {
            // Al seleccionar una categoría, pasamos a la Activity de detalles
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("categoria", categoria);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
    }
}

