package com.example.parqlink;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import android.content.pm.PackageManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Build;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.os.Parcelable;

import com.example.parqlink.Backend_Integration.ApiClient;
import com.example.parqlink.Backend_Integration.ApiService;
import com.example.parqlink.DTO.NfcScanRequest;
import com.example.parqlink.DTO.ParkingSessionResponse;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;

import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NfcReaderActivity extends AppCompatActivity {

    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;
    private NfcAdapter nfcAdapter;

  private ReproductorSonidos reproductor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        NotificationHelper.crearCanal(this);

          reproductor = new ReproductorSonidos(this);
          reproductor.cargarSonido(R.raw.notificacion_sonido);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC no disponible", Toast.LENGTH_LONG).show();
            finish();
        }

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);
        IntentFilter[] filters = new IntentFilter[]{new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)};
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, filters, null);

    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {

            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            String tagText = null;

            if (rawMsgs != null && rawMsgs.length > 0) {
                NdefMessage msg = (NdefMessage) rawMsgs[0];
                NdefRecord record = msg.getRecords()[0];
                tagText = getTextFromNdefRecord(record);
            }

            if (tagText == null) {
                Toast.makeText(this, "No se pudo leer el texto del tag", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs = getSharedPreferences("ParqLinkPrefs", MODE_PRIVATE);
            String token = prefs.getString("jwt_token", null);
            if (token == null) {
                Toast.makeText(this, "Debes iniciar sesión", Toast.LENGTH_SHORT).show();
                return;
            }

            int colorAzul = Color.parseColor("#5170FD");

            NfcScanRequest scan = new NfcScanRequest();
            scan.setNfcTagId(tagText);

            ApiService api = ApiClient.getApiService();
            api.scan("Bearer " + token, scan).enqueue(new Callback<ParkingSessionResponse>() {
                @Override
                public void onResponse(Call<ParkingSessionResponse> call, Response<ParkingSessionResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ParkingSessionResponse session = response.body();

                        if (session.getEndTime() != null) {
                            // Finalizar sesión
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean("session_active", false);
                            editor.remove("session_start_time");
                            editor.remove("session_tag");
                            editor.remove("start_time_ms");
                            editor.apply();


                            String rawStart = session.getStartTime();
                            String trimmedStart = rawStart.split("\\.")[0];
                            LocalDateTime startDateTime = LocalDateTime.parse(trimmedStart);
                            long startTimeMillisRaw = startDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                            long now = System.currentTimeMillis();
                            long startTimeMillis = Math.min(startTimeMillisRaw, now);

                            String rawEnd = session.getEndTime().split("\\.")[0];
                            LocalDateTime endDateTime = LocalDateTime.parse(rawEnd);
                            long endTimeMillis = endDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

                            long durationMillis = endTimeMillis - startTimeMillis;
                            long durationHours = (long) Math.ceil(durationMillis / (1000.0 * 60 * 60));

                            double montoTotal = session.getTotalCost();

                            AlertDialog dialog = new AlertDialog.Builder(NfcReaderActivity.this, R.style.FixedWhiteDialog)
                                    .setTitle("Sesión Finalizada")
                                    .setMessage("Sesión cerrada correctamente.\n" +
                                            "Monto total a pagar: €" + String.format("%.2f", montoTotal))
                                    .setPositiveButton("Pagar ahora", (dialogInterface, which) -> {
                                        iniciarPagoConGooglePay(montoTotal);
                                    })
                                    .create();

                            dialog.setCancelable(false);
                            dialog.setCanceledOnTouchOutside(false);

                            dialog.setOnShowListener(d -> {
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(colorAzul);
                            });

                            dialog.show();



                            stopService(new Intent(NfcReaderActivity.this, NfcSessionService.class));

                            NotificationHelper.mostrarNotificacionSesionFinalizada(
                                    NfcReaderActivity.this,
                                    session.getParkingName(),
                                    durationMillis,
                                    session.getTotalCost() != null ? session.getTotalCost() : 0.0
                            );

                            reproductor.reproducir(R.raw.notificacion_sonido);

                        } else {

                            String rawStart = session.getStartTime();
                            String trimmedStart = rawStart.split("\\.")[0];
                            LocalDateTime startDateTime = LocalDateTime.parse(trimmedStart);
                            long startTimeMillis = startDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                            long now = System.currentTimeMillis();
                            long tiempoInicioReal = Math.min(startTimeMillis, now);

                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean("session_active", true);
                            editor.putLong("session_start_time", tiempoInicioReal);
                            editor.putString("session_tag", session.getParkingName());
                            editor.putLong("start_time_ms", tiempoInicioReal);
                            editor.apply();


                            Intent serviceIntent = new Intent(NfcReaderActivity.this, NfcSessionService.class);
                            serviceIntent.putExtra("nombre_parking", session.getParkingName());
                            serviceIntent.putExtra("price_per_hour", session.getPricePerHour());
                            ContextCompat.startForegroundService(NfcReaderActivity.this, serviceIntent);

                            NotificationHelper.crearNotificacionPersistente(
                                    NfcReaderActivity.this, session.getParkingName(), startTimeMillis, session.getPricePerHour()
                            );
                            reproductor.reproducir(R.raw.notificacion_sonido);


                            AlertDialog dialog = new AlertDialog.Builder(NfcReaderActivity.this, R.style.FixedWhiteDialog)
                                    .setTitle("Sesión Iniciada")
                                    .setMessage("Escaneo exitoso: sesión en curso.")
                                    .setPositiveButton("OK", null)
                                    .create();

                            dialog.show();
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(colorAzul);


                        }

                    } else {
                        Toast.makeText(NfcReaderActivity.this, "No se pudo iniciar/finalizar la sesión", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ParkingSessionResponse> call, Throwable t) {
                    Log.e("API_ERROR", "Error al escanear", t);
                    Toast.makeText(NfcReaderActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void iniciarPagoConGooglePay(double montoTotal) {
        PaymentsClient paymentsClient = Wallet.getPaymentsClient(
                this,
                new Wallet.WalletOptions.Builder()
                        .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                        .build()
        );

        PaymentDataRequest request = GooglePayUtils.createPaymentDataRequest(montoTotal);
        if (request == null) return;

        AutoResolveHelper.resolveTask(
                paymentsClient.loadPaymentData(request),
                this,
                LOAD_PAYMENT_DATA_REQUEST_CODE
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOAD_PAYMENT_DATA_REQUEST_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    if (data != null) {
                        PaymentData paymentData = PaymentData.getFromIntent(data);
                        String paymentInfo = paymentData.toJson();

                        Toast.makeText(this, "Pago realizado con éxito", Toast.LENGTH_LONG).show();

                        AlertDialog dialog = new AlertDialog.Builder(this, R.style.FixedWhiteDialog)
                                .setTitle("Pago exitoso")
                                .setMessage("Gracias por utilizar ParqLink. Su transacción ha sido completada con éxito")
                                .setPositiveButton("OK", null)
                                .create();

                        dialog.setOnShowListener(dialogInterface -> {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAzulSuave));
                        });

                        dialog.show();

                    }
                    break;

                case RESULT_CANCELED:
                    Toast.makeText(this, "Pago cancelado", Toast.LENGTH_SHORT).show();
                    break;

                case AutoResolveHelper.RESULT_ERROR:
                    Toast.makeText(this, "Error al procesar el pago", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }


    private String getTextFromNdefRecord(NdefRecord record) {
        try {
            byte[] payload = record.getPayload();
            String textEncoding = ((payload[0] & 0x80) == 0) ? "UTF-8" : "UTF-16";
            int languageCodeLength = payload[0] & 0x3F;
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
