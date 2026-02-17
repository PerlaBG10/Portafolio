package com.example.parqlink;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.auth0.android.jwt.JWT;
import com.example.parqlink.Backend_Integration.ApiClient;
import com.example.parqlink.Backend_Integration.ApiService;
import com.example.parqlink.DTO.AuthResponse;
import com.example.parqlink.DTO.LoginRequest;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button btnLogin, btnRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences("ParqLinkPrefs", MODE_PRIVATE);
        String token = preferences.getString("jwt_token", null);

        if (token != null && !isTokenExpired(token)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        } else {
            preferences.edit().clear().apply();
        }



        setContentView(R.layout.activity_inicio_login);

        if (getIntent().getBooleanExtra("logout", false)) {
            Toast.makeText(this, "Sesión cerrada exitosamente", Toast.LENGTH_SHORT).show();
        }

        final boolean[] isPasswordVisible = {false};
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(v -> login());
        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        passwordInput.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (passwordInput.getRight() - passwordInput.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {

                    v.performClick();

                    if (isPasswordVisible[0]) {
                        passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        passwordInput.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_contrasena, 0, R.drawable.icon_contrasena_ocultar, 0);
                    } else {
                        passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        passwordInput.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_contrasena, 0, R.drawable.icon_contrasena_mostrar, 0);
                    }
                    passwordInput.setSelection(passwordInput.getText().length());
                    isPasswordVisible[0] = !isPasswordVisible[0];
                    return true;
                }
            }
            return false;
        });


    }

    private boolean isTokenExpired(String token) {
        try {
            JWT jwt = new JWT(token);
            Date expiresAt = jwt.getExpiresAt();
            return expiresAt == null || expiresAt.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }


    private void login() {
        String email = emailInput.getText().toString().trim();
        String pass = passwordInput.getText().toString().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidEmail(email)) {
            emailInput.setError("Formato de correo inválido");
            emailInput.requestFocus();
            return;
        }



        LoginRequest request = new LoginRequest(email, pass);
        ApiService api = ApiClient.getApiService();

        api.login(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getToken() != null) {
                    String token = response.body().getToken();

                    SharedPreferences prefs = getSharedPreferences("ParqLinkPrefs", MODE_PRIVATE);
                    prefs.edit()
                            .putBoolean("isLoggedIn", true)
                            .putString("jwt_token", token)
                            .putString("user_email", email)
                            .apply();


                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Credenciales inválidas o error del servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
}