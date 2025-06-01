package com.arishay.pawconnect;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Get the logo ImageView
        ImageView logoImage = findViewById(R.id.splashLogo);

        // Load and start the bounce animation
        Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.logo_bounce);
        logoImage.startAnimation(bounceAnim);

        // Create a handler to delay navigation to LoginActivity
        new Handler().postDelayed(() -> {
            // Start LoginActivity
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            // Close this activity
            finish();
        }, SPLASH_DURATION);
    }
} 