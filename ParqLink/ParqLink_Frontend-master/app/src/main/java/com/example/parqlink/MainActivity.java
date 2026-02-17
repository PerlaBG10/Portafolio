package com.example.parqlink;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.parqlink.Backend_Integration.ApiClient;
import com.example.parqlink.Backend_Integration.ApiService;
import com.example.parqlink.DTO.ParkingResponse;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.maps.android.SphericalUtil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MapView mapView;
    private GoogleMap mMap;
    private RecyclerView recyclerView;
    private ParkingAdapter adapter;
    public static List<ParkingResponse> parkingList = new ArrayList<>();
    private ImageButton btnfilter, btnReload, btnMenu, btnSearch ,btnDetalles;

    private NfcAdapter nfcAdapter;
    private boolean sessionActive;
    private boolean mostrandoFavoritos = false;
    private Button btnNFCSession;
    private FusedLocationProviderClient fusedLocationClient;

    private double currentLat = 0.0;
    private double currentLng = 0.0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//         Para que pueda abrir en android studio subraya la linea 79 a la 88

//        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
//        if (!nfcAdapter.isEnabled()) {
//            new AlertDialog.Builder(this)
//                    .setTitle("Activar NFC")
//                    .setMessage("Por favor, activa NFC para continuar.")
//                    .setPositiveButton("Ir a ajustes", (dialog, which) -> {
//                        startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
//                    })
//                    .setNegativeButton("Cancelar", null)
//                    .show();
//        }


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SharedPreferences prefs = getSharedPreferences("ParqLinkPrefs", MODE_PRIVATE);
        String email = prefs.getString("user_email", null);

        if (email != null) {
            FavoritesManager.init(this, email);
        }

        navigationView = findViewById(R.id.navView);
        drawerLayout = findViewById(R.id.drawerLayout);

        recyclerView = findViewById(R.id.recyclerViewParkings);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ParkingAdapter(parkingList, false, this::moverMapaAParking);
        recyclerView.setAdapter(adapter);
        loadParkings();

        btnfilter = findViewById(R.id.btnFilter);
        btnReload = findViewById(R.id.btnReload);
        btnSearch = findViewById(R.id.btnSearch);
        btnMenu = findViewById(R.id.btnMenu);
        btnDetalles = findViewById(R.id.btnDetalles);

        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));


        ordenarParkingsPorCercania();

         prefs = getSharedPreferences("ParqLinkPrefs", MODE_PRIVATE);
        long inicioSesion = prefs.getLong("inicioSesion", 0);
        if (inicioSesion > 0) {
            mostrarSesionActiva(inicioSesion);
        } else {
            mostrarBotonUbicacion();
        }

        mapView = findViewById(R.id.mapView);
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(this);
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_profile) {
                startActivity(new Intent(this, Profile.class));
            } else if (id == R.id.nav_history) {
                startActivity(new Intent(this, Historialdeactividad.class));
            } else if (id == R.id.nav_cerrarsesion) {
                SharedPreferences preferences = getSharedPreferences("ParqLinkPrefs", MODE_PRIVATE);
                preferences.edit()
                        .clear()
                        .apply();

                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("logout", true);
                startActivity(intent);

                finish();
            }
            return true;
        });


        btnNFCSession = findViewById(R.id.btnNFCSession);

        sessionActive = prefs.getBoolean("session_active", false);


        SharedPreferences finalPrefs = prefs;
        btnNFCSession.setOnClickListener(v -> {
            sessionActive = finalPrefs.getBoolean("session_active", false);

            if (sessionActive) {
                startActivity(new Intent(MainActivity.this, NfcReaderActivity.class));;

                btnDetalles.setVisibility(View.VISIBLE);

            } else {
                startActivity(new Intent(MainActivity.this, NfcReaderActivity.class));
                btnDetalles.setVisibility(View.GONE);
            }
        });


        btnfilter.setOnClickListener(v -> {
            String[] opciones = {"Distancia", "Precio", "Min/Max Distancia", "Min/Max Precio"};
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.FixedWhiteDialog);
            builder.setTitle("Ordenar por");

            builder.setItems(opciones, (dialog, which) -> {
                if (which == 0) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
                        return;
                    }

                    fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                        if (location != null) {
                            ApiClient.getApiService().getFilteredParkings(
                                    null, null,
                                    location.getLatitude(),
                                    location.getLongitude(),
                                    null, null,
                                    "distance", "asc",
                                    0, 20
                            ).enqueue(new Callback<List<ParkingResponse>>() {
                                @Override
                                public void onResponse(Call<List<ParkingResponse>> call, Response<List<ParkingResponse>> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        parkingList.clear();
                                        parkingList.addAll(response.body());
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(MainActivity.this, "No se pudo ordenar por cercanía", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<ParkingResponse>> call, Throwable t) {
                                    Toast.makeText(MainActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                } else if (which == 1) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
                        return;
                    }

                    fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                        if (location != null) {
                            ApiClient.getApiService().getFilteredParkings(
                                    null, null,
                                    location.getLatitude(),
                                    location.getLongitude(),
                                    null, null,
                                    "price", "asc",
                                    0, 20
                            ).enqueue(new Callback<List<ParkingResponse>>() {
                                @Override
                                public void onResponse(Call<List<ParkingResponse>> call, Response<List<ParkingResponse>> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        parkingList.clear();
                                        parkingList.addAll(response.body());
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(MainActivity.this, "No se pudo ordenar por precio", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<ParkingResponse>> call, Throwable t) {
                                    Toast.makeText(MainActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(MainActivity.this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else if (which == 2) {

                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
                        return;
                    }

                    fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                        if (location != null) {
                            currentLat = location.getLatitude();
                            currentLng = location.getLongitude();
                        } else {
                            currentLat = 0.0;
                            currentLng = 0.0;
                        }

                        mostrarDialogoFiltroDistancia();
                    });
                } else if (which == 3) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
                        return;
                    }

                    fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                        if (location != null) {
                            currentLat = location.getLatitude();
                            currentLng = location.getLongitude();

                            mostrarDialogoFiltroPrecio(currentLat, currentLng);
                        } else {
                            Toast.makeText(MainActivity.this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show();
                        }
                    });
                }


            });

            builder.create().show();
        });


        btnReload.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Actualizando ubicación...", Toast.LENGTH_SHORT).show();

            mostrandoFavoritos = false;

            loadParkings();

            adapter = new ParkingAdapter(parkingList, false, this::moverMapaAParking);
            recyclerView.setAdapter(adapter);
        });

        btnSearch.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.FixedWhiteDialog);
            builder.setTitle("Buscar parking");

            final EditText input = new EditText(MainActivity.this);
            input.setHint("Escribe el nombre...");
            input.setHintTextColor(Color.GRAY);
            input.setTextColor(Color.BLACK);
            input.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.editext_backgroundsearch));
            input.setPadding(40, 30, 40, 30);

            LinearLayout layout = new LinearLayout(MainActivity.this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(50, 40, 50, 10);
            layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
            layout.addView(input);
            builder.setView(layout);

            builder.setPositiveButton("Buscar", (dialog, which) -> {
                String query = input.getText().toString().toLowerCase().trim();
                List<ParkingResponse> resultados = new ArrayList<>();
                for (ParkingResponse p : parkingList) {
                    if (p.getName().toLowerCase().contains(query)) {
                        resultados.add(p);
                    }
                }

                if (resultados.isEmpty()) {
                    Toast.makeText(MainActivity.this, "No se encontraron coincidencias", Toast.LENGTH_SHORT).show();
                }

                adapter = new ParkingAdapter(resultados, false, this::moverMapaAParking);
                recyclerView.setAdapter(adapter);
            });

            builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

            AlertDialog dialog = builder.create();
            dialog.setOnShowListener(dlg -> {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#4F6DF6"));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#4F6DF6"));
            });

            dialog.show();
        });


        SharedPreferences finalPrefs1 = prefs;
        btnDetalles.setOnClickListener(v -> {
            String sessionTag = finalPrefs1.getString("session_tag", "Desconocido");
            long sessionStartTime = finalPrefs1.getLong("session_start_time", 0);

            Intent intent = new Intent(MainActivity.this, DetailsSession.class);
            intent.putExtra("session_tag", sessionTag);
            intent.putExtra("session_start_time", sessionStartTime);
            startActivity(intent);
        });



        ImageButton btnFavorite = findViewById(R.id.btnFavorite);
        btnFavorite.setOnClickListener(v -> {
            mostrandoFavoritos = !mostrandoFavoritos;
            List<ParkingResponse> favoritos = mostrandoFavoritos ? FavoritesManager.getFavorites() : parkingList;
            adapter = new ParkingAdapter(favoritos, mostrandoFavoritos, this::moverMapaAParking);
            recyclerView.setAdapter(adapter);
        });
    }

    private void mostrarDialogoFiltroDistancia() {
        AlertDialog.Builder distanciaDialog = new AlertDialog.Builder(MainActivity.this, R.style.FixedWhiteDialog);
        distanciaDialog.setTitle("Filtrar por distancia (km)");

        LinearLayout layout = new LinearLayout(MainActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 30, 40, 10);

        EditText inputMin = new EditText(MainActivity.this);
        inputMin.setHint("Distancia mínima (km)");
        inputMin.setTextColor(Color.BLACK);
        inputMin.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
        inputMin.setHintTextColor(Color.GRAY);
        inputMin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(inputMin);

        EditText inputMax = new EditText(MainActivity.this);
        inputMax.setHint("Distancia máxima (km)");
        inputMax.setTextColor(Color.BLACK);
        inputMax.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
        inputMax.setHintTextColor(Color.GRAY);
        inputMax.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(inputMax);

        distanciaDialog.setView(layout);

        distanciaDialog.setPositiveButton("Aplicar", (d, w) -> {
            String minStr = inputMin.getText().toString().trim();
            String maxStr = inputMax.getText().toString().trim();

            Double minKm = minStr.isEmpty() ? 0.0 : Double.parseDouble(minStr);
            Double maxKm = maxStr.isEmpty() ? Double.MAX_VALUE : Double.parseDouble(maxStr);

            filtrarParkingsPorDistancia(minKm, maxKm);
        });

        distanciaDialog.setNegativeButton("Cancelar", (d, w) -> d.cancel());

        AlertDialog dialogDistancia = distanciaDialog.create();
        dialogDistancia.show();
        dialogDistancia.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#4F6DF6"));
        dialogDistancia.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#4F6DF6"));
    }


    private void filtrarParkingsPorDistancia(Double minKm, Double maxKm) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                double currentLat = location.getLatitude();
                double currentLng = location.getLongitude();

                ApiClient.getApiService().getFilteredParkings(
                        null, null,
                        currentLat, currentLng,
                        null, null,
                        "distance", "asc",
                        0, 50
                ).enqueue(new Callback<List<ParkingResponse>>() {
                    @Override
                    public void onResponse(Call<List<ParkingResponse>> call, Response<List<ParkingResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<ParkingResponse> listaFiltrada = new ArrayList<>();

                            for (ParkingResponse parking : response.body()) {
                                double distancia = calcularDistanciaKm(currentLat, currentLng, parking.getLatitude(), parking.getLongitude());

                                if (distancia >= minKm && distancia <= maxKm) {
                                    listaFiltrada.add(parking);
                                }
                            }

                            parkingList.clear();
                            parkingList.addAll(listaFiltrada);
                            adapter.notifyDataSetChanged();

                            if (listaFiltrada.isEmpty()) {
                                Toast.makeText(MainActivity.this, "No se encontraron parkings en ese rango de distancia", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Error al obtener parkings", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ParkingResponse>> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(MainActivity.this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoFiltroPrecio(double lat, double lng) {
        AlertDialog.Builder precioDialog = new AlertDialog.Builder(MainActivity.this, R.style.FixedWhiteDialog);
        precioDialog.setTitle("Filtrar por precio (€)");

        LinearLayout layout = new LinearLayout(MainActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 30, 40, 10);

        EditText inputMin = new EditText(MainActivity.this);
        inputMin.setHint("Precio mínimo (€)");
        inputMin.setTextColor(Color.BLACK);
        inputMin.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
        inputMin.setHintTextColor(Color.GRAY);
        inputMin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(inputMin);

        EditText inputMax = new EditText(MainActivity.this);
        inputMax.setHint("Precio máximo (€)");
        inputMax.setTextColor(Color.BLACK);
        inputMax.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
        inputMax.setHintTextColor(Color.GRAY);
        inputMax.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(inputMax);

        precioDialog.setView(layout);

        precioDialog.setPositiveButton("Aplicar", (d, w) -> {
            String minStr = inputMin.getText().toString().trim();
            String maxStr = inputMax.getText().toString().trim();

            Double minPrice = minStr.isEmpty() ? 0.0 : Double.parseDouble(minStr);
            Double maxPrice = maxStr.isEmpty() ? Double.MAX_VALUE : Double.parseDouble(maxStr);

            ApiClient.getApiService().getFilteredParkings(
                    null, null,
                    lat, lng,
                    null, maxPrice,
                    "price", "asc",
                    0, 20
            ).enqueue(new Callback<List<ParkingResponse>>() {
                @Override
                public void onResponse(Call<List<ParkingResponse>> call, Response<List<ParkingResponse>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<ParkingResponse> listaFiltrada = new ArrayList<>();

                        for (ParkingResponse parking : response.body()) {
                            if (parking.getPricePerHour() >= minPrice && parking.getPricePerHour() <= maxPrice) {
                                listaFiltrada.add(parking);
                            }
                        }

                        parkingList.clear();
                        parkingList.addAll(listaFiltrada);
                        adapter.notifyDataSetChanged();

                        if (listaFiltrada.isEmpty()) {
                            Toast.makeText(MainActivity.this, "No se encontraron parkings en ese rango de precio", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Error al obtener parkings", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<ParkingResponse>> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        });

        precioDialog.setNegativeButton("Cancelar", (d, w) -> d.cancel());

        AlertDialog dialogPrecio = precioDialog.create();
        dialogPrecio.show();
        dialogPrecio.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#4F6DF6"));
        dialogPrecio.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#4F6DF6"));
    }


    private double calcularDistanciaKm(double lat1, double lng1, double lat2, double lng2) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lng1, lat2, lng2, results);
        return results[0] / 1000.0;
    }

    private void loadParkings() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                double userLat = location.getLatitude();
                double userLng = location.getLongitude();

                ApiClient.getApiService().getFilteredParkings(
                        null, null,
                        userLat, userLng,
                        null, null,
                        null, null,
                        0, 20
                ).enqueue(new retrofit2.Callback<List<ParkingResponse>>() {
                    @Override
                    public void onResponse(Call<List<ParkingResponse>> call, retrofit2.Response<List<ParkingResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            parkingList.clear();
                            parkingList.addAll(response.body());
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(MainActivity.this, "Error al cargar parkings", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ParkingResponse>> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(MainActivity.this, "Ubicación no disponible", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarTextoBotonNFC(boolean sessionActive) {
        if (sessionActive) {
            btnNFCSession.setText("Cerrar sesión");
        } else {
            btnNFCSession.setText("Iniciar sesión");
        }
    }
    private void ocultarSesionActiva() {
        btnDetalles.setVisibility(View.GONE);
    }
    private void mostrarSesionActiva(long inicioSesion) {
        btnDetalles.setVisibility(View.VISIBLE);
    }

    private void mostrarBotonUbicacion() {
        btnDetalles.setVisibility(View.GONE);
    }

    private void habilitarUbicacionUsuario() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
                ordenarParkingsPorCercania();
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
        }
    }

    private void ordenarParkingsPorCercania() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                double userLat = location.getLatitude();
                double userLng = location.getLongitude();

                ApiService apiService = ApiClient.getApiService();
                Call<List<ParkingResponse>> call = apiService.getFilteredParkings(
                        null,
                        null,
                        userLat,
                        userLng,
                        null,
                        null,
                        "distance",
                        "asc",
                        0,
                        100
                );

                call.enqueue(new Callback<List<ParkingResponse>>() {
                    @Override
                    public void onResponse(Call<List<ParkingResponse>> call, Response<List<ParkingResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            parkingList.clear();
                            parkingList.addAll(response.body());
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(MainActivity.this, "Error al obtener parkings ordenados", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ParkingResponse>> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    private void moverMapaAParking(ParkingResponse parking) {
        if (mMap != null) {
            LatLng ubicacion = new LatLng(parking.getLatitude(), parking.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 16));
            mMap.addMarker(new MarkerOptions().position(ubicacion).title(parking.getName()));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14));
                mMap.addMarker(new MarkerOptions()
                        .position(userLocation)
                        .title("Tu ubicación")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            }

            for (ParkingResponse parking : parkingList) {
                LatLng locationParking = new LatLng(parking.getLatitude(), parking.getLongitude());
                mMap.addMarker(new MarkerOptions().position(locationParking).title(parking.getName()));
            }
        });

        habilitarUbicacionUsuario();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();

        SharedPreferences prefs = getSharedPreferences("ParqLinkPrefs", MODE_PRIVATE);
        sessionActive = prefs.getBoolean("session_active", false);

        actualizarTextoBotonNFC(sessionActive);

        long inicioSesion = prefs.getLong("session_start_time", 0);
        if (sessionActive && inicioSesion > 0) {
            mostrarSesionActiva(inicioSesion);
        } else {
            ocultarSesionActiva();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) mapView.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            habilitarUbicacionUsuario();
        }
    }
}
