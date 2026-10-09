package com.example.balancetech_tcc_equipe05.Activites;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.balancetech_tcc_equipe05.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        VideoView videoView = findViewById(R.id.videoView);

        Uri videoUri = Uri.parse(
                "android.resource://" + getPackageName() + "/" + R.raw.screen
        );

        videoView.setVideoURI(videoUri);

        // Zoom na animação
        videoView.setScaleX(1f);
        videoView.setScaleY(1f);

        videoView.start();

        // Quando a animação terminar
        videoView.setOnCompletionListener(mp -> {

            // Pequena espera para garantir que o último frame apareça
            new Handler().postDelayed(() -> {

                Intent intent = new Intent(
                        SplashActivity.this,
                        BemVindoActivity.class
                );

                startActivity(intent);
                finish();

            }, 300);
        });
    }
}

