package com.example.tetris;

import android.graphics.Color;
import android.util.Log;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Figura {
    private int[][] forma;
    private int x, y;
    private int color;

    public Figura(String tipo) {
        cambiarForma(tipo);
        x = 5;
        y = 0;
        color = generarColorAleatorio();
    }

    public int[][] getForma() {
        return forma;
    }

    public void setForma(int[][] nuevaForma) {
        this.forma = nuevaForma;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getColor() {
        return color;
    }

    public void mover(int dx, int dy) {
        x += dx;
        y += dy;
    }

    public void moverIzquierda() {
        x--;
    }

    public void moverDerecha() {
        x++;
    }

    public void cambiarForma(String tipo) {
        switch (tipo) {
            case "cuadrado":
                forma = new int[][]{
                        {1, 1},
                        {1, 1}
                };
                break;
            case "T":
                forma = new int[][]{
                        {0, 1, 0},
                        {1, 1, 1}
                };
                break;
            case "L":
                forma = new int[][]{
                        {1, 0, 0},
                        {1, 1, 1}
                };
                break;
            case "S":
                forma = new int[][]{
                        {0, 1, 1},
                        {1, 1, 0}
                };
                break;
            case "Z":
                forma = new int[][]{
                        {1, 1, 0},
                        {0, 1, 1}
                };
                break;
            case "I":
                forma = new int[][]{
                        {1, 1, 1, 1}
                };
                break;
        }
    }

    private int generarColorAleatorio() {
        Random rand = new Random();
        int red = rand.nextInt(256);
        int green = rand.nextInt(256);
        int blue = rand.nextInt(256);

        int color = Color.rgb(red, green, blue);
        Log.d("ColorGenerado", "Color generado: #" + Integer.toHexString(color));

        return color;
    }


    public void moverHaciaAbajoConTemporizador() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                bajar();
            }
        }, 0, 500);
    }

    public void bajar() {
        y++;
    }
}
