package com.example.tetris;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Tablero {

    private static final int TAMAÑO_CELDA = 50;

    private static final int FILAS = 20;
    private static final int COLUMNAS = 10;
    private int[][] tablero;
    public Figura piezaActual;
    private Timer timer;

    private int puntuacion = 0;
    private SonidoJuego sonidoJuego;
    private Context context;

    public Tablero(Context context) {
        tablero = new int[FILAS][COLUMNAS];
        this.context = context;

        sonidoJuego = new SonidoJuego(context);
        nuevaPieza();
        iniciarTemporizador();
    }

    private void iniciarTemporizador() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                bajarPieza();
            }
        }, 0, 500);
    }

    public void nuevaPieza() {
        String[] tipos = {"cuadrado", "T", "L", "S", "Z", "I"};
        String tipoAleatorio = tipos[new Random().nextInt(tipos.length)];
        piezaActual = new Figura(tipoAleatorio);

        if (!esMovimientoValido(piezaActual.getX(), piezaActual.getY(), piezaActual.getForma())) {
            finDelJuego();
        }
    }

    public void bajarPieza() {
        if (!moverPieza(0, 1)) {
            fijarPieza();
            nuevaPieza();

            if (!esMovimientoValido(piezaActual.getX(), piezaActual.getY(), piezaActual.getForma())) {
                if (tableroLleno()) {
                    timer.cancel();
                    finDelJuego();
                }
            }
        }
    }

    public boolean tableroLleno() {
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                if (tablero[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }


    public boolean moverPieza(int dx, int dy) {
        int nuevaX = piezaActual.getX() + dx;
        int nuevaY = piezaActual.getY() + dy;

        if (esMovimientoValido(nuevaX, nuevaY, piezaActual.getForma())) {
            piezaActual.mover(dx, dy);
            return true;
        }
        return false;
    }

    public boolean esMovimientoValido(int x, int y, int[][] forma) {
        for (int i = 0; i < forma.length; i++) {
            for (int j = 0; j < forma[i].length; j++) {
                if (forma[i][j] != 0) {
                    int nuevoX = x + j;
                    int nuevoY = y + i;

                    if (nuevoX < 0 || nuevoX >= COLUMNAS || nuevoY >= FILAS || nuevoY < 0) {
                        return false;
                    }

                    if (nuevoY >= 0 && tablero[nuevoY][nuevoX] != 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void fijarPieza() {
        int[][] forma = piezaActual.getForma();
        int x = piezaActual.getX();
        int y = piezaActual.getY();
        for (int i = 0; i < forma.length; i++) {
            for (int j = 0; j < forma[i].length; j++) {
                if (forma[i][j] != 0) {
                    tablero[y + i][x + j] = 1;
                }
            }
        }
        limpiarLineas();
    }
    public void limpiarLineas() {
        for (int i = 0; i < FILAS; i++) {
            boolean llena = true;
            for (int j = 0; j < COLUMNAS; j++) {
                if (tablero[i][j] == 0) {
                    llena = false;
                    break;
                }
            }
            if (llena) {
                eliminarLinea(i);
                puntuacion += 100;
                guardarPuntuacion(true);
                sonidoJuego.reproducirSonidoEliminarFila();
            }
        }
    }
    public void guardarPuntuacion(boolean borrarAntes) {
        SharedPreferences prefs = context.getSharedPreferences("Scores", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        if (borrarAntes) {
            editor.putString("Puntuaciones", "");
            editor.apply();
        }

        String historial = prefs.getString("Puntuaciones", "");

        if (!historial.contains(String.valueOf(puntuacion))) {
            if (!historial.isEmpty()) {
                historial = historial + puntuacion + ",";
            } else {
                historial = String.valueOf(puntuacion);
            }
        }

        editor.putString("Puntuaciones", historial);
        editor.apply();
    }


    public void eliminarLinea(int fila) {
        for (int i = fila; i > 0; i--) {
            System.arraycopy(tablero[i - 1], 0, tablero[i], 0, COLUMNAS);
        }
        for (int i = 0; i < COLUMNAS; i++) {
            tablero[0][i] = 0;
        }
    }

    public void rotarPieza() {
        int[][] forma = piezaActual.getForma();
        int n = forma.length;
        int m = forma[0].length;
        int[][] nuevaForma = new int[m][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                nuevaForma[j][n - 1 - i] = forma[i][j];
            }
        }

        if (esMovimientoValido(piezaActual.getX(), piezaActual.getY(), nuevaForma)) {
            piezaActual.setForma(nuevaForma);
            sonidoJuego.reproducirSonidoRotar();
        }
    }

    public void finDelJuego() {
        guardarPuntuacion(false);
        if (timer != null) {
            timer.cancel();
        }
        Intent i = new Intent(context, GameOver.class);
        context.startActivity(i);
    }



    public int[][] getTablero() {
        return tablero;
    }

    public Figura getPiezaActual() {
        return piezaActual;
    }

    public void dibujarPieza(Canvas canvas) {
        int[][] forma = piezaActual.getForma();
        int color = piezaActual.getColor();
        Paint paint = new Paint();
        paint.setColor(color);

        Log.d("ColorPieza", "Color de la pieza: " + Integer.toHexString(color));

        for (int i = 0; i < forma.length; i++) {
            for (int j = 0; j < forma[i].length; j++) {
                if (forma[i][j] != 0) {
                    canvas.drawRect(
                            (piezaActual.getX() + j) * TAMAÑO_CELDA,
                            (piezaActual.getY() + i) * TAMAÑO_CELDA,
                            (piezaActual.getX() + j + 1) * TAMAÑO_CELDA,
                            (piezaActual.getY() + i + 1) * TAMAÑO_CELDA,
                            paint);
                }
            }
        }
    }



    public void liberarRecursosSonidos() {
        sonidoJuego.liberarSonidos();
    }
}
