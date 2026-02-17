package com.example.parqlink;


import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.core.app.NotificationManagerCompat;

public class NfcSessionService extends Service {

    private Handler handler;
    private Runnable updater;
    private Runnable warningRunnable;
    private long startTime;
    private String nombreParking;
    private double pricePerHour;
    private ReproductorSonidos reproductor;
    private int lastWarnedHour = -1;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences prefs = getSharedPreferences("ParqLinkPrefs", MODE_PRIVATE);
        startTime = prefs.getLong("start_time_ms", System.currentTimeMillis());
        nombreParking = intent.getStringExtra("nombre_parking");
        pricePerHour = intent.getDoubleExtra("price_per_hour", 0.0);
        NotificationHelper.crearCanal(this);
        Notification notification = NotificationHelper.crearNotificacionPersistente(this, nombreParking, startTime, pricePerHour);
        startForeground(NotificationHelper.SESSION_NOTIFICATION_ID, notification);

        reproductor = new ReproductorSonidos(this);
        reproductor.cargarSonido(R.raw.notificacion_sonido);

        handler = new Handler(Looper.getMainLooper());

        updater = new Runnable() {
            @Override
            public void run() {
                long elapsedMillis = System.currentTimeMillis() - startTime;

                int minutos = (int) ((elapsedMillis / (1000 * 60)) % 60);
                int horas = (int) (elapsedMillis / (1000 * 60 * 60));
                if (minutos == 45 && lastWarnedHour != horas) {
                    NotificationHelper.mostrarAdvertencia(NfcSessionService.this);
                    reproductor.reproducir(R.raw.notificacion_sonido);
                    lastWarnedHour = horas;
                }

                Notification updated = NotificationHelper.crearNotificacionPersistente(
                        NfcSessionService.this, nombreParking, startTime, pricePerHour);

                NotificationManagerCompat.from(NfcSessionService.this)
                        .notify(NotificationHelper.SESSION_NOTIFICATION_ID, updated);
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(updater);

        warningRunnable = () -> {
            NotificationHelper.mostrarAdvertencia(NfcSessionService.this);
            reproductor.reproducir(R.raw.notificacion_sonido);
        };
        handler.postDelayed(warningRunnable, 60 * 60 * 1000);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (handler != null) {
            if (updater != null) handler.removeCallbacks(updater);
            if (warningRunnable != null) handler.removeCallbacks(warningRunnable);
        }
        stopForeground(true);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}


