package com.arishay.pawconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AdopterActivity extends AppCompatActivity {

    Button browsePetsBtn, myRequestsBtn, favouritePetsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adopter_dashboard); // Linked to new XML layout

        // Buttons
        browsePetsBtn = findViewById(R.id.browsePetsBtn);
        myRequestsBtn = findViewById(R.id.myRequestsBtn);
        favouritePetsBtn = findViewById(R.id.favouritePetsBtn);

        browsePetsBtn.setOnClickListener(v ->
                startActivity(new Intent(this, AdopterBrowseActivity.class)));

        myRequestsBtn.setOnClickListener(v ->
                startActivity(new Intent(this, AdopterRequestsActivity.class)));

        favouritePetsBtn.setOnClickListener(v ->
                startActivity(new Intent(this, FavouritePetsActivity.class)));

        FooterNavigation.setupFooterNavigation(this);
    }
}
