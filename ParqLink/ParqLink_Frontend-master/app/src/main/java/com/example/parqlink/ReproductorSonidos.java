package com.example.parqlink;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import androidx.annotation.RawRes;

import java.util.HashMap;

public class ReproductorSonidos {

    private SoundPool soundPool;
    private HashMap<Integer, Integer> sonidos;
    private Context context;

    public ReproductorSonidos(Context context) {
        this.context = context;
        sonidos = new HashMap<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(5, android.media.AudioManager.STREAM_MUSIC, 0);
        }
    }

    public void cargarSonido(@RawRes int idRaw) {
        int soundId = soundPool.load(context, idRaw, 1);
        sonidos.put(idRaw, soundId);
    }

    public void reproducir(@RawRes int idRaw) {
        Integer soundId = sonidos.get(idRaw);
        if (soundId != null) {
            soundPool.play(soundId, 1, 1, 0, 0, 1);
        }
    }

    public void liberar() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}

