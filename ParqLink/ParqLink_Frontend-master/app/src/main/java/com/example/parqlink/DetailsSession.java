package com.example.parqlink;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parqlink.Backend_Integration.ApiClient;
import com.example.parqlink.Backend_Integration.ApiService;
import com.example.parqlink.DTO.ParkingSessionResponse;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsSession extends AppCompatActivity {

    private TextView tvTag, tvDuration, tvAddress, tvPrice, tvTotalPrice;
    private long sessionStartTime;
    private double basePricePerHour;

    private final Handler handler = new Handler();

    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            long elapsedMillis = System.currentTimeMillis() - sessionStartTime;

            String tiempoTranscurrido = formatTime(elapsedMillis);
            tvDuration.setText("Duración: " + tiempoTranscurrido);

            long horasFacturadas = Math.max(1, (long) Math.ceil(elapsedMillis / (1000.0 * 60 * 60)));
            double precioTotal = basePricePerHour * horasFacturadas;
            tvTotalPrice.setText(String.format("Precio Total: €%.2f", precioTotal));

            handler.postDelayed(this, 1000);
        }

    };

    private long parseTimestampToMillis(String timestamp) {
        try {
            if (timestamp.contains(".")) {
                timestamp = timestamp.substring(0, timestamp.indexOf("."));
            }
            LocalDateTime ldt = LocalDateTime.parse(timestamp);
            ZonedDateTime zdt = ldt.atZone(ZoneId.of("UTC"));
            return zdt.toInstant().toEpochMilli();
        } catch (Exception e) {
            e.printStackTrace();
            return System.currentTimeMillis();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_details_session);

        tvTag = findViewById(R.id.tvSessionTag);
        tvDuration = findViewById(R.id.tvSessionDuration);
        tvAddress = findViewById(R.id.tvSessionAddress);
        tvPrice = findViewById(R.id.tvSessionPrice);
        tvTotalPrice = findViewById(R.id.tvSessionTotalPrice);


        SharedPreferences prefs = getSharedPreferences("ParqLinkPrefs", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

        if (token == null) {
            Toast.makeText(this, "Token no encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ApiService apiService = ApiClient.getApiService();
        Call<List<ParkingSessionResponse>> call = apiService.getUserSessions("Bearer " + token);
        call.enqueue(new Callback<List<ParkingSessionResponse>>() {
            @Override
            public void onResponse(Call<List<ParkingSessionResponse>> call, Response<List<ParkingSessionResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ParkingSessionResponse activeSession = null;

                    for (ParkingSessionResponse session : response.body()) {
                        if (session.getEndTime() == null) {
                            activeSession = session;
                            break;
                        }
                    }

                    if (activeSession != null) {
                        tvTag.setText("Parking: " + activeSession.getParkingName());
                        tvAddress.setText("Dirección: " + activeSession.getAddress());
                        tvPrice.setText("Precio por hora: €" + activeSession.getPricePerHour());
                        basePricePerHour = activeSession.getPricePerHour();
                        tvPrice.setText("Precio por hora: €" + basePricePerHour);

                        SharedPreferences.Editor editor = prefs.edit();

                        if (!prefs.contains("start_time_ms")) {
                            long parsedTime = parseTimestampToMillis(activeSession.getStartTime());
                            editor.putLong("start_time_ms", parsedTime);
                            editor.apply();
                            sessionStartTime = parsedTime;
                        } else {
                            sessionStartTime = prefs.getLong("start_time_ms", System.currentTimeMillis());
                        }
                        handler.post(updateRunnable);
                    } else {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.remove("start_time_ms");
                        editor.apply();

                        Toast.makeText(DetailsSession.this, "No hay sesión activa", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                } else {
                    Toast.makeText(DetailsSession.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ParkingSessionResponse>> call, Throwable t) {
                Toast.makeText(DetailsSession.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(updateRunnable);
    }

    private String formatTime(long diffMillis) {
        long segundos = (diffMillis / 1000) % 60;
        long minutos = (diffMillis / (1000 * 60)) % 60;
        long horas = diffMillis / (1000 * 60 * 60);
        return String.format("%02d:%02d:%02d", horas, minutos, segundos);
    }
}
