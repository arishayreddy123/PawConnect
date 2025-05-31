package com.arishay.pawconnect;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AdopterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);

        TextView welcomeText = new TextView(this);
        welcomeText.setText("Welcome, Adopter!");
        welcomeText.setTextSize(20f);
        layout.setBackgroundColor(Color.parseColor("#ff009d"));
        layout.addView(welcomeText);

        // Button: Browse Pets
        Button browsePetsBtn = new Button(this);
        browsePetsBtn.setText("Browse Pets");
        browsePetsBtn.setOnClickListener(v ->
                startActivity(new Intent(this, AdopterBrowseActivity.class)));
        layout.addView(browsePetsBtn);

        // Button: My Requests
        Button myRequestsBtn = new Button(this);
        myRequestsBtn.setText("My Requests");
        myRequestsBtn.setOnClickListener(v ->
                startActivity(new Intent(this, AdopterRequestsActivity.class)));
        layout.addView(myRequestsBtn);

        // Button: My Favourites
        Button myFavouritesBtn = new Button(this);
        myFavouritesBtn.setText("My Favourites");
        myFavouritesBtn.setOnClickListener(v ->
                startActivity(new Intent(this, MyFavouritesActivity.class)));
        layout.addView(myFavouritesBtn);

        setContentView(layout);
    }
}
