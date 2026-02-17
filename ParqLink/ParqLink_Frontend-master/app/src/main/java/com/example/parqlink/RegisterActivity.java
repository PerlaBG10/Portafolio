package com.example.parqlink;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parqlink.Backend_Integration.ApiClient;
import com.example.parqlink.Backend_Integration.ApiService;
import com.example.parqlink.DTO.AuthResponse;
import com.example.parqlink.DTO.RegisterRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText registerEmail, registerPassword, registerName;
    private Button btnCreateAccount, btnVolver;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_register);

        final boolean[] isPasswordVisible = {false};
        registerEmail = findViewById(R.id.registerEmail);
        registerPassword = findViewById(R.id.registerPassword);
        registerName = findViewById(R.id.registerName);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        btnVolver = findViewById(R.id.btnVolver);

        registerPassword.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (registerPassword.getRight() - registerPassword.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {

                    v.performClick();

                    if (isPasswordVisible[0]) {
                        registerPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        registerPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_contrasena, 0, R.drawable.icon_contrasena_ocultar, 0);
                    } else {
                        registerPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        registerPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_contrasena, 0, R.drawable.icon_contrasena_mostrar, 0);
                    }
                    registerPassword.setSelection(registerPassword.getText().length());
                    isPasswordVisible[0] = !isPasswordVisible[0];
                    return true;
                }
            }
            return false;
        });


        btnVolver.setOnClickListener(view -> finish());

        btnCreateAccount.setOnClickListener(v -> {
            String email = registerEmail.getText().toString().trim();
            String password = registerPassword.getText().toString().trim();
            String name = registerName.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidEmail(email)) {
                registerEmail.setError("Formato de correo inválido");
                registerEmail.requestFocus();
                return;
            }

            String errorPass = validarContrasena(password);
            if (errorPass != null) {
                Toast.makeText(this, errorPass, Toast.LENGTH_LONG).show();
                registerPassword.requestFocus();
                return;
            }

            RegisterRequest request = new RegisterRequest(name, email, password);
            ApiService api = ApiClient.getApiService();

            api.register(request).enqueue(new Callback<AuthResponse>() {
                @Override
                public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(RegisterActivity.this, "Cuenta creada correctamente", Toast.LENGTH_SHORT).show();

                        SharedPreferences prefs = getSharedPreferences("ParqLinkPrefs", MODE_PRIVATE);

                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "No se pudo registrar", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<AuthResponse> call, Throwable t) {
                    Toast.makeText(RegisterActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    private String validarContrasena(String contrasena) {
        if (contrasena == null) return "Contraseña vacía";

        boolean tieneMayuscula = false;
        boolean tieneMinuscula = false;
        boolean tieneNumero = false;
        boolean tieneEspecial = false;

        for (int i = 0; i < contrasena.length(); i++) {
            char c = contrasena.charAt(i);

            if (Character.isUpperCase(c)) {
                tieneMayuscula = true;
            } else if (Character.isLowerCase(c)) {
                tieneMinuscula = true;
            } else if (Character.isDigit(c)) {
                tieneNumero = true;
            } else if (isCaracterEspecial(c)) {
                tieneEspecial = true;
            }
        }

        if (!tieneMayuscula) return "La contraseña debe tener al menos una letra mayúscula.";
        if (!tieneMinuscula) return "La contraseña debe tener al menos una letra minúscula.";
        if (!tieneNumero) return "La contraseña debe tener al menos un número.";
        if (!tieneEspecial) return "La contraseña debe tener al menos un carácter especial.";

        return null;
    }

    private boolean isCaracterEspecial(char c) {
        String especiales = "!@#$%^&*()-_=+[]{}|;:'\",.<>?/`~\\";
        return especiales.indexOf(c) >= 0;
    }
}
