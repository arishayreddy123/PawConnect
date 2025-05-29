package com.arishay.pawconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RescuerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));

        TextView welcomeText = new TextView(this);
        welcomeText.setText("Welcome, Rescuer!");
        welcomeText.setTextSize(24);
        welcomeText.setPadding(0, 0, 0, 48);
        layout.addView(welcomeText);

        Button addListingBtn = new Button(this);
        addListingBtn.setText("Add New Pet Listing");
        addListingBtn.setOnClickListener(v ->
                startActivity(new Intent(this, AddListingActivity.class))
        );
        layout.addView(addListingBtn);

        Button viewListingsBtn = new Button(this);
        viewListingsBtn.setText("View My Listings");
        viewListingsBtn.setOnClickListener(v ->
                startActivity(new Intent(this, MyListingsActivity.class))
        );
        layout.addView(viewListingsBtn);

        Button viewRequestsBtn = new Button(this);
        viewRequestsBtn.setText("View Adoption Requests");
        viewRequestsBtn.setOnClickListener(v ->
                startActivity(new Intent(this, RescuerRequestsActivity.class))
        );
        layout.addView(viewRequestsBtn);

        setContentView(layout);
    }
}
