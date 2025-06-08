package com.arishay.pawconnect;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.RelativeLayout;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;

public class FavouritePetsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create root layout
        RelativeLayout layout = new RelativeLayout(this);
        layout.setBackgroundColor(Color.parseColor("#ff009d"));
        layout.setPadding(32, 32, 32, 32);

        // Add a TextView for placeholder
        TextView textView = new TextView(this);
        textView.setText("❤️ Your favourite pets will show up here!");
        textView.setTextSize(20f);
        textView.setTextColor(Color.WHITE);
        textView.setId(TextView.generateViewId());

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(textView, params);

        // Set layout
        setContentView(layout);

        // Add the footer navigation
        getLayoutInflater().inflate(R.layout.footer_navigation, layout, true);
        FooterNavigation.setupFooterNavigation(this);
    }
}
