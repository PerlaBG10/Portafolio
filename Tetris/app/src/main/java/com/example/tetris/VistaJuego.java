package com.example.tetris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

public class VistaJuego extends SurfaceView implements SurfaceHolder.Callback {
    private Tablero tablero;
    private Paint paint;
    private final int tamañoCelda = 100;

    private Random random;

    public VistaJuego(Context context) {
        super(context);
        getHolder().addCallback(this);
        tablero = new Tablero(context);
        paint = new Paint();
        random = new Random();

    }

    public VistaJuego() {

        super(null);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        new Thread(() -> {
            while (true) {
                Canvas canvas = holder.lockCanvas();
                if (canvas != null) {
                    dibujarTablero(canvas);
                    holder.unlockCanvasAndPost(canvas);
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (x < getWidth() / 3) {
                    tablero.moverPieza(-1, 0);
                } else if (x > getWidth() * 2 / 3) {
                    tablero.moverPieza(1, 0);
                } else {
                    tablero.moverPieza(0, 1);
                }

                if (x >= getWidth() / 3 && x <= getWidth() * 2 / 3 && y <= getHeight() / 3) {
                    tablero.rotarPieza();
                }

                break;
        }

        return true;
    }



    private void dibujarTablero(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        int[][] grid = tablero.getTablero();
        int[][] forma = tablero.getPiezaActual().getForma();
        int x = tablero.getPiezaActual().getX();
        int y = tablero.getPiezaActual().getY();

        paint.setColor(Color.GRAY);
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] != 0) {
                    canvas.drawRect(j * tamañoCelda, i * tamañoCelda,
                            (j + 1) * tamañoCelda, (i + 1) * tamañoCelda, paint);
                }
            }
        }

        int colorAleatorio = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        paint.setColor(colorAleatorio);        for (int i = 0; i < forma.length; i++) {
            for (int j = 0; j < forma[i].length; j++) {
                if (forma[i][j] != 0) {
                    canvas.drawRect((x + j) * tamañoCelda, (y + i) * tamañoCelda,
                            (x + j + 1) * tamañoCelda, (y + i + 1) * tamañoCelda, paint);
                }
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {}
}
