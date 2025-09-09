package com.example.guiamapas;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PuntoInteresDetailActivity extends AppCompatActivity {

    private TextView nombreTextView;
    private TextView descripcionTextView;
    private TextView horarioTextView;
    private ImageView imagenImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_punto_interes_detail);

        // Inicializamos las vistas
        nombreTextView = findViewById(R.id.nombreTextView);
        descripcionTextView = findViewById(R.id.descripcionTextView);
        horarioTextView = findViewById(R.id.horarioTextView);
        imagenImageView = findViewById(R.id.imagenImageView);

        // Recibimos los datos del punto de interés desde el Intent
        Intent intent = getIntent();

        // Obtener los datos del Intent
        String nombre = intent.getStringExtra("nombre");
        String descripcion = intent.getStringExtra("descripcion");
        String horario = intent.getStringExtra("horario");
        int imagenId = intent.getIntExtra("imagenId", 0); // Valor por defecto 0 si no se pasa imagen
        double latitud = intent.getDoubleExtra("latitud", 0.0); // Valor por defecto 0.0 si no se pasa
        double longitud = intent.getDoubleExtra("longitud", 0.0); // Valor por defecto 0.0 si no se pasa

        // Comprobamos que los datos no sean nulos o vacíos
        if (nombre != null && !nombre.isEmpty()) {
            nombreTextView.setText(nombre);
        } else {
            nombreTextView.setText("Nombre no disponible");
        }

        if (descripcion != null && !descripcion.isEmpty()) {
            descripcionTextView.setText(descripcion);
        } else {
            descripcionTextView.setText("Descripción no disponible");
        }

        if (horario != null && !horario.isEmpty()) {
            horarioTextView.setText(horario);
        } else {
            horarioTextView.setText("Horario no disponible");
        }

        if (imagenId != 0) {
            imagenImageView.setImageResource(imagenId);
        } else {
            imagenImageView.setImageResource(R.drawable.deafult_image); // Imagen por defecto en caso de que no se pase ninguna
        }

        // Configuramos el botón para mostrar el mapa
        findViewById(R.id.btnMostrarMapa).setOnClickListener(v -> {
            // Verificamos si los datos de latitud y longitud son válidos antes de abrir el mapa
            if (latitud != 0.0 && longitud != 0.0) {
                Intent mapIntent = new Intent(PuntoInteresDetailActivity.this, Mapactivity.class);
                mapIntent.putExtra("nombre", nombre);
                mapIntent.putExtra("descripcion", descripcion);
                mapIntent.putExtra("latitud", latitud);
                mapIntent.putExtra("longitud", longitud);
                startActivity(mapIntent);
            } else {
                // Si no hay coordenadas válidas, mostramos un mensaje de error
                Toast.makeText(PuntoInteresDetailActivity.this, "Coordenadas no disponibles", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
