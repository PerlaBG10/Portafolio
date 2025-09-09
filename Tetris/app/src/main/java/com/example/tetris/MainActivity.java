package com.example.tetris;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConstraintLayout layout = findViewById(R.id.main);

        Button btnIr = findViewById(R.id.Jugar);
        btnIr.setBackgroundColor(Color.rgb(44, 44, 133));
        btnIr.setTextColor(getResources().getColor(android.R.color.white));
        btnIr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VistaJuego vistaJuego = new VistaJuego(MainActivity.this);

                setContentView(vistaJuego);
            }
        });


        Button btnOtro = findViewById(R.id.Score);
        btnOtro.setBackgroundColor(Color.rgb(44, 44, 133));
        btnOtro.setTextColor(getResources().getColor(android.R.color.white));
        btnOtro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Puntuacion.class);
                startActivity(intent);
            }
        });
    }
}
