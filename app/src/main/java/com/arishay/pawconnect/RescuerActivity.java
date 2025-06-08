package com.arishay.pawconnect;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
        layout.setBackgroundColor(Color.parseColor("#ff009d")); // Vibrant pink

        // Logo (large)
        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.logo); // Make sure this exists
        LinearLayout.LayoutParams logoParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                500
        );
        logo.setLayoutParams(logoParams);
        layout.addView(logo);

        // Welcome text
        TextView welcomeText = new TextView(this);
        welcomeText.setText("🐾 Welcome, Rescuer! 🐶");
        welcomeText.setTextSize(28);
        welcomeText.setTextColor(Color.WHITE);
        welcomeText.setPadding(0, 40, 0, 28);
        welcomeText.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        layout.addView(welcomeText);

        // Tagline
        TextView tagline = new TextView(this);
        tagline.setText("You're the hero they’ve been waiting for 💞");
        tagline.setTextSize(18);
        tagline.setTextColor(Color.WHITE);
        tagline.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        layout.addView(tagline);

        // Emoji divider
        TextView divider = new TextView(this);
        divider.setText("🐕‍🦺 🐾 🐈‍⬛ 🐾 🐩");
        divider.setTextSize(22);
        divider.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        divider.setPadding(0, 30, 0, 30);
        layout.addView(divider);

        // Button layout params
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        btnParams.setMargins(0, 20, 0, 20);

        // Button color
        int softYellow = Color.parseColor("#FFD54F");

        // Add New Listing
        Button addListingBtn = createStyledButton("➕ Add New Pet Listing", softYellow);
        addListingBtn.setOnClickListener(v ->
                startActivity(new Intent(this, AddListingActivity.class))
        );
        layout.addView(addListingBtn, btnParams);

        // View Listings
        Button viewListingsBtn = createStyledButton("📋 View My Listings", softYellow);
        viewListingsBtn.setOnClickListener(v ->
                startActivity(new Intent(this, MyListingsActivity.class))
        );
        layout.addView(viewListingsBtn, btnParams);

        // View Requests
        Button viewRequestsBtn = createStyledButton("📨 View Adoption Requests", softYellow);
        viewRequestsBtn.setOnClickListener(v ->
                startActivity(new Intent(this, RescuerRequestsActivity.class))
        );
        layout.addView(viewRequestsBtn, btnParams);

        // Footer quote
        TextView footer = new TextView(this);
        footer.setText("“Saving lives, one paw at a time.” 🐾");
        footer.setTextSize(14);
        footer.setTextColor(Color.WHITE);
        footer.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        footer.setPadding(0, 50, 0, 0);
        layout.addView(footer);

        setContentView(layout);

        FooterNavigation.setupFooterNavigation(this);
    }

    private Button createStyledButton(String text, int bgColor) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextSize(18);
        button.setTextColor(Color.BLACK);
        button.setPadding(24, 24, 24, 24);
        button.setAllCaps(false);
        button.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button_pink));
        return button;
    }
}
