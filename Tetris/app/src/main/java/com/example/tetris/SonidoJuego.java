package com.example.tetris;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class SonidoJuego {

    private SoundPool soundPool;
    private int sonidoRotar;
    private int sonidoEliminarFila;


    public SonidoJuego(Context context) {
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        sonidoRotar = soundPool.load(context, R.raw.rotar, 1);

        sonidoEliminarFila = soundPool.load(context, R.raw.eliminar_fila, 1);
    }

    public void reproducirSonidoRotar() {
        soundPool.play(sonidoRotar, 1, 1, 0, 0, 1);
    }

    public void reproducirSonidoEliminarFila() {
        soundPool.play(sonidoEliminarFila, 1, 1, 0, 0, 1);
    }



    public void liberarSonidos() {
        soundPool.release();
    }
}
