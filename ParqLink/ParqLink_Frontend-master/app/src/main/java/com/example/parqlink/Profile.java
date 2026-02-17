package com.example.parqlink;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.parqlink.Backend_Integration.ApiClient;
import com.example.parqlink.Backend_Integration.ApiService;
import com.example.parqlink.DTO.ProfileResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class Profile extends AppCompatActivity {

    TextView tvEmail, tvNombre;
    Button btnVolver;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navdrawer_activity_profile);

        tvNombre = findViewById(R.id.tvNombre);
        tvEmail = findViewById(R.id.tvEmail);

        btnVolver = findViewById(R.id.btnVolver);

        preferences = getSharedPreferences("ParqLinkPrefs", MODE_PRIVATE);
        editor = preferences.edit();

        String savedNombre = preferences.getString("savedName", "Usuario");
        String savedEmail = preferences.getString("savedEmail", "No registrado");

        tvNombre.setText("Nombre: " + savedNombre);
        tvEmail.setText("Email: " + savedEmail);

        cargarPerfilDesdeBackend();

        btnVolver.setOnClickListener(view -> finish());
    }

    private void cargarPerfilDesdeBackend() {
        String token = preferences.getString("jwt_token", null);

        if (token == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getApiService();

        apiService.getUserProfile("Bearer " + token).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String nombre = response.body().getName();
                    String email = response.body().getEmail();

                    tvNombre.setText("Nombre:\n" + nombre);
                    tvEmail.setText("Email:\n" + email);

                    editor.putString("savedName", nombre);
                    editor.putString("savedEmail", email);
                    editor.apply();
                } else {
                    Toast.makeText(Profile.this, "Error al obtener perfil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Toast.makeText(Profile.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
