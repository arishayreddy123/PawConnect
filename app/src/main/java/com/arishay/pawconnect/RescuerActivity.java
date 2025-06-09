package com.arishay.pawconnect;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class RescuerActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Root layout
        RelativeLayout rootLayout = new RelativeLayout(this);
        rootLayout.setBackgroundColor(Color.parseColor("#ff009d"));

        // Scrollable content area
        ScrollView scrollView = new ScrollView(this);
        scrollView.setId(View.generateViewId());  // Unique ID for layout rules
        RelativeLayout.LayoutParams scrollParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        scrollParams.addRule(RelativeLayout.ABOVE, R.id.footerNavigation);

        LinearLayout contentLayout = new LinearLayout(this);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setPadding(32, 32, 32, 32);
        scrollView.addView(contentLayout);

        // --- Add content to contentLayout ---
        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.logo);
        logo.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 500
        ));
        contentLayout.addView(logo);

        TextView welcomeText = new TextView(this);
        welcomeText.setText("🐾 Welcome, Rescuer! 🐶");
        welcomeText.setTextSize(28);
        welcomeText.setTextColor(Color.WHITE);
        welcomeText.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        welcomeText.setPadding(0, 40, 0, 28);
        contentLayout.addView(welcomeText);

        TextView tagline = new TextView(this);
        tagline.setText("You're the hero they've been waiting for 💞");
        tagline.setTextSize(18);
        tagline.setTextColor(Color.WHITE);
        tagline.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        contentLayout.addView(tagline);

        TextView divider = new TextView(this);
        divider.setText("🐕‍🦺 🐾 🐈‍⬛ 🐾 🐩");
        divider.setTextSize(22);
        divider.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        divider.setPadding(0, 30, 0, 30);
        contentLayout.addView(divider);

        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        btnParams.setMargins(0, 20, 0, 20);
        int softYellow = Color.parseColor("#FFD54F");

        Button addListingBtn = createStyledButton("➕ Add New Pet Listing", softYellow);
        addListingBtn.setOnClickListener(v -> startActivity(new Intent(this, AddListingActivity.class)));
        contentLayout.addView(addListingBtn, btnParams);

        Button viewListingsBtn = createStyledButton("📋 View My Listings", softYellow);
        viewListingsBtn.setOnClickListener(v -> startActivity(new Intent(this, MyListingsActivity.class)));
        contentLayout.addView(viewListingsBtn, btnParams);

        Button viewRequestsBtn = createStyledButton("📨 View Adoption Requests", softYellow);
        viewRequestsBtn.setOnClickListener(v -> startActivity(new Intent(this, RescuerRequestsActivity.class)));
        contentLayout.addView(viewRequestsBtn, btnParams);

        TextView footerQuote = new TextView(this);
        footerQuote.setText("\"Saving lives, one paw at a time.\" 🐾");
        footerQuote.setTextSize(14);
        footerQuote.setTextColor(Color.WHITE);
        footerQuote.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        footerQuote.setPadding(0, 50, 0, 0);
        contentLayout.addView(footerQuote);

        rootLayout.addView(scrollView, scrollParams);

        // --- Footer (inflated at the bottom) ---
        FrameLayout footerContainer = new FrameLayout(this);
        footerContainer.setId(R.id.footerNavigation);  // Must match the scroll above
        RelativeLayout.LayoutParams footerParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        footerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        getLayoutInflater().inflate(R.layout.footer_navigation, footerContainer, true);
        rootLayout.addView(footerContainer, footerParams);

        // Set the layout
        setContentView(rootLayout);

        // Setup footer logic
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
