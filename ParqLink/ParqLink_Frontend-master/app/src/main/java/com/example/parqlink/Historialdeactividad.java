package com.example.parqlink;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parqlink.Backend_Integration.ApiClient;
import com.example.parqlink.Backend_Integration.ApiService;
import com.example.parqlink.DTO.ParkingSessionResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Historialdeactividad extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ParkingSessionAdapter adapter;

    private Button btnVolverHistorial;
    private List<ParkingSessionResponse> sessionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navdrawer_activity_historialactividad);
        recyclerView = findViewById(R.id.navdrawer_activity_historial);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btnVolverHistorial=findViewById(R.id.btnVolverHistorial);
        obtenerHistorialDelServidor();

        btnVolverHistorial.setOnClickListener(view -> finish());

    }

    private void obtenerHistorialDelServidor() {
        String token = getAuthToken();

        if (token == null) {
            Toast.makeText(this, "Token no encontrado. Inicia sesión nuevamente.", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api = ApiClient.getApiService();
        String bearerToken = "Bearer " + token;

        Call<List<ParkingSessionResponse>> call = api.getUserSessions(bearerToken);
        call.enqueue(new Callback<List<ParkingSessionResponse>>() {
            @Override
            public void onResponse(Call<List<ParkingSessionResponse>> call, Response<List<ParkingSessionResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    sessionList = response.body();
                    adapter = new ParkingSessionAdapter(sessionList);
                    recyclerView.setAdapter(adapter);
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Sin detalle";
                        Toast.makeText(Historialdeactividad.this, "Error " + response.code() + ": " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(Historialdeactividad.this, "Error " + response.code(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ParkingSessionResponse>> call, Throwable t) {
                Toast.makeText(Historialdeactividad.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getAuthToken() {
        SharedPreferences prefs = getSharedPreferences("ParqLinkPrefs", MODE_PRIVATE);
        return prefs.getString("jwt_token", null);
    }
}
