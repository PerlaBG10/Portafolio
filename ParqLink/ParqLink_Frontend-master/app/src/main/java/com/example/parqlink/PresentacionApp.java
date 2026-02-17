package com.example.parqlink;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class PresentacionApp extends AppCompatActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_apppresentacion);

        videoView = findViewById(R.id.videoView);

        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.intro);
        videoView.setVideoURI(videoUri);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                int videoWidth = mp.getVideoWidth();
                int videoHeight = mp.getVideoHeight();

                float videoProportion = (float) videoWidth / (float) videoHeight;

                int screenWidth = getResources().getDisplayMetrics().widthPixels;
                int targetWidth = (int) (screenWidth * 0.7);
                int targetHeight = (int) (targetWidth / videoProportion);

                ViewGroup.LayoutParams params = videoView.getLayoutParams();
                params.width = targetWidth;
                params.height = targetHeight;
                videoView.setLayoutParams(params);

                videoView.start();
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

                Intent intent;
                if (isLoggedIn) {
                    intent = new Intent(PresentacionApp.this, MainActivity.class);
                } else {
                    intent = new Intent(PresentacionApp.this, LoginActivity.class);
                }

                startActivity(intent);
                finish();
            }
        });
    }
}
