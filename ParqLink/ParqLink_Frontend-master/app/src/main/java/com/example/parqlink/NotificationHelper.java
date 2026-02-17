package com.example.parqlink;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.util.Locale;

public class NotificationHelper {

    public static final String CHANNEL_ID = "nfc_session_channel";
    public static final int SESSION_NOTIFICATION_ID = 1;
    public static final int WARNING_NOTIFICATION_ID = 2;
    private static Handler handler;
    private static Runnable updater;

    public static void crearCanal(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Sesión NFC",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Notificaciones de parking en uso");
            channel.enableVibration(false);
            channel.setSound(null, null);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }



    public static void mostrarAdvertencia(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.app_logo)
                .setContentTitle("Aviso")
                .setContentText("¡Quedan 15 minutos para cumplir 1 hora!")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                        == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(context).notify(WARNING_NOTIFICATION_ID, builder.build());
        }
    }

    public static void cancelarNotificaciones(Context context) {
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.cancel(SESSION_NOTIFICATION_ID);

        if (handler != null && updater != null) {
            handler.removeCallbacks(updater);
            handler = null;
            updater = null;
        }
    }
    public static void mostrarNotificacionSesionFinalizada(Context context, String nombreParking, long durationMillis, double totalCost) {
        cancelarNotificaciones(context);

        long segundos = (durationMillis / 1000) % 60;
        long minutos = (durationMillis / (1000 * 60)) % 60;
        long horas = durationMillis / (1000 * 60 * 60);
        String tiempo = String.format("%02d:%02d:%02d", horas, minutos, segundos);

        String mensaje = "Duración total: " + tiempo + "\nCosto total: " + String.format(Locale.getDefault(), "%.2f€", totalCost);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.app_logo)
                .setContentTitle("Sesión finalizada en " + nombreParking)
                .setContentText(mensaje)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(mensaje))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat.from(context).notify(SESSION_NOTIFICATION_ID, notification);
    }

    public static Notification crearNotificacionPersistente(Context context, String nombreParking, long startTime, double pricePerHour) {
        long diffMillis = System.currentTimeMillis() - startTime;

        long segundos = (diffMillis / 1000) % 60;
        long minutos = (diffMillis / (1000 * 60)) % 60;
        long horas = diffMillis / (1000 * 60 * 60);
        String tiempo = String.format("%02d:%02d:%02d", horas, minutos, segundos);

        long horasFacturadas = Math.max(1, (long) Math.ceil(diffMillis / (1000.0 * 60 * 60)));
        double costeActual = pricePerHour * horasFacturadas;

        String textoNotificacion = "Tiempo: " + tiempo + "\nCosto actual: " + String.format(Locale.getDefault(), "%.2f€", costeActual);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.app_logo)
                .setContentTitle("Sesión en " + nombreParking)
                .setContentText(textoNotificacion)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(textoNotificacion))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .build();
    }

}
