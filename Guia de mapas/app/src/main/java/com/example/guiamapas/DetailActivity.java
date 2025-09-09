package com.example.guiamapas;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PuntoInteresAdapter adapter;
    private List<PuntoInteres> puntosInteres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Recuperamos los datos del Intent
        String nombre = getIntent().getStringExtra("nombre");
        String descripcion = getIntent().getStringExtra("descripcion");
        String horario = getIntent().getStringExtra("horario");
        int imagenId = getIntent().getIntExtra("imagenId", -1); // ID de la imagen
        double latitud = getIntent().getDoubleExtra("latitud", 0);
        double longitud = getIntent().getDoubleExtra("longitud", 0);

        TextView nombreTextView = findViewById(R.id.textNombre);
        TextView descripcionTextView = findViewById(R.id.textDescripcion);
        TextView horarioTextView = findViewById(R.id.textHorario);
        ImageView imagenView = findViewById(R.id.imageView);

        nombreTextView.setText(nombre);
        descripcionTextView.setText(descripcion);
        horarioTextView.setText(horario);

        if (horario != null && !horario.isEmpty()) {
            horarioTextView.setText(horario);
        }

        if (imagenId != -1) {
            imagenView.setImageResource(imagenId);
        }

        String categoria = getIntent().getStringExtra("categoria");

        if (categoria == null) {
            Toast.makeText(this, "Categoría no válida", Toast.LENGTH_SHORT).show();
            return;
        }

        // Inicializamos el RecyclerView
        recyclerView = findViewById(R.id.recyclerViewPuntosInteres);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializamos la lista de puntos de interés según la categoría
        puntosInteres = getPuntosInteres(categoria);

        // Adaptador para el RecyclerView
        adapter = new PuntoInteresAdapter(puntosInteres);
        recyclerView.setAdapter(adapter);
    }

    // Método que carga los puntos de interés basados en la categoría
    private List<PuntoInteres> getPuntosInteres(String categoria) {
        List<PuntoInteres> puntos = new ArrayList<>();
        switch (categoria) {
            case "Restaurantes":
                puntos.add(new PuntoInteres("Restaurante La Plaza", "Restaurantes", 90.000, -10.000, "Un restaurante acogedor en el centro de la ciudad, ideal para disfrutar de platos locales e internacionales.", false, R.drawable.laplaza, "Lunes a Domingo: 12:00 - 22:00"));
                puntos.add(new PuntoInteres("Restaurante El Club", "Restaurantes", 40.4236, -3.7095, "Famoso por su comida típica española, un lugar perfecto para disfrutar de tapas y platos tradicionales.", false, R.drawable.elclub, "Martes a Domingo: 13:00 - 23:00"));
                puntos.add(new PuntoInteres("Café de Oriente", "Restaurantes", 40.4187, -3.7110, "Con vistas al Palacio Real, es el lugar ideal para tomar un café mientras admiras una de las mejores vistas de Madrid.", false, R.drawable.cafe, "Todos los días: 08:00 - 20:00"));
                break;
            case "Museos":
                puntos.add(new PuntoInteres("Museo del Prado", "Museos", 40.4138, -3.6921, "Uno de los museos más importantes del mundo, que alberga una de las colecciones de arte más destacadas de Europa, con obras de Velázquez y Goya.", true, R.drawable.museoprado, "Lunes a Sábado: 10:00 - 20:00"));
                puntos.add(new PuntoInteres("Museo Reina Sofía", "Museos", 40.4085, -3.6916, "Centro de arte moderno y contemporáneo, famoso por su impresionante colección de Picasso, incluyendo 'Guernica'.", true, R.drawable.museosofia, "Martes a Domingo: 10:00 - 21:00"));
                puntos.add(new PuntoInteres("Museo Thyssen", "Museos", 40.4200, -3.6935, "En este museo se encuentra una de las colecciones de arte privado más importantes, que abarca desde el Renacimiento hasta el arte moderno.", true, R.drawable.museothyssen, "Lunes a Domingo: 10:00 - 19:00"));
                break;
            case "Parques":
                puntos.add(new PuntoInteres("Parque del Retiro", "Parques", 40.4148, -3.6828, "Un extenso parque en el corazón de Madrid, ideal para pasear, hacer picnic o disfrutar de actividades al aire libre.", false, R.drawable.parqueretiro, "Abierto todos los días: 06:00 - 22:00"));
                puntos.add(new PuntoInteres("Parque de la Vaguada", "Parques", 40.4417, -3.7470, "Un parque tranquilo con amplias zonas verdes, perfecto para relajarse y disfrutar de la naturaleza.", false, R.drawable.parquevaguada, "Abierto todos los días: 08:00 - 20:00"));
                break;
            case "Monumentos":
                puntos.add(new PuntoInteres("Puerta del Sol", "Monumentos", 40.4168, -3.7038, "Es uno de los lugares más emblemáticos de Madrid, conocido por ser el centro geográfico de la ciudad y un punto de encuentro popular.", true, R.drawable.puertasol, "Siempre abierto"));
                puntos.add(new PuntoInteres("Plaza Mayor", "Monumentos", 40.4153, -3.7074, "Esta histórica plaza es famosa por su arquitectura renacentista y por albergar eventos culturales y celebraciones en el corazón de Madrid.", true, R.drawable.plazamayor, "Siempre abierto"));
                break;
            case "Teatros":
                puntos.add(new PuntoInteres("Teatro Español", "Teatros", 40.4171, -3.7052, "Un teatro histórico que ofrece una amplia gama de obras, desde clásicos hasta producciones contemporáneas.", false, R.drawable.teatroespanol, "Miércoles a Domingo: 18:00 - 23:00"));
                puntos.add(new PuntoInteres("Teatro de la Zarzuela", "Teatros", 40.4335, -3.7083, "Dedicado a la zarzuela, este teatro ofrece una experiencia única de música y teatro, con un enfoque en el género musical español tradicional.", false, R.drawable.teatroarzuela, "Miércoles a Domingo: 17:00 - 22:00"));
                break;
            default:
                break;
        }
        return puntos;
    }


}
