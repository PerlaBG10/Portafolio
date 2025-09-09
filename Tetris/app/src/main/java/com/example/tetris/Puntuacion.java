package com.example.tetris;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Puntuacion extends AppCompatActivity {

    private TextView Historial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puntuacion);

        Historial = findViewById(R.id.historial);

        mostrarHistorial();

        Button btnRegre = findViewById(R.id.Regresar);
        btnRegre.setBackgroundColor(Color.rgb(44, 44, 133));
        btnRegre.setTextColor(getResources().getColor(android.R.color.white));

        btnRegre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Puntuacion.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void mostrarHistorial() {
        SharedPreferences prefs = getSharedPreferences("Scores", Context.MODE_PRIVATE);
        String puntuacionesGuardadas = prefs.getString("Puntuaciones", "");

        if (!puntuacionesGuardadas.isEmpty()) {
            Historial.setText("Historial de puntuaciones\n" + puntuacionesGuardadas.replace(",", "\n"));
        } else {
            Historial.setText("No hay puntuaciones anteriores.");
        }
    }

    public void guardarPuntuacion(int puntuacion) {
        SharedPreferences prefs = getSharedPreferences("Scores", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String historial = prefs.getString("Puntuaciones", "");

        if (!historial.isEmpty()) {
            historial = historial + "\n"+ + puntuacion ;
        } else {
            historial = String.valueOf(puntuacion);
        }

        editor.putString("Puntuaciones", historial);
        editor.apply();
        mostrarHistorial();
    }
}
