package com.arishay.pawconnect;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FooterNavigation {

    public static void setupFooterNavigation(Activity activity) {
        try {
            ImageView backButton = activity.findViewById(R.id.btnBack);
            ImageView homeButton = activity.findViewById(R.id.btnHome);
            ImageView profileButton = activity.findViewById(R.id.btnProfile);

            if (backButton != null) {
                backButton.setOnClickListener(v -> {
                    activity.onBackPressed(); // Always go to previous screen
                });
            }

            if (homeButton != null) {
                homeButton.setOnClickListener(v -> {
                    Intent intent = new Intent(activity, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);
                    activity.finish();
                });
            }

            if (profileButton != null) {
                profileButton.setOnClickListener(v -> {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (currentUser != null) {
                        // User is logged in – go to UserProfileActivity
                        Intent intent = new Intent(activity, UserProfileActivity.class);
                        activity.startActivity(intent);
                    } else {
                        // Not logged in – go to LoginActivity
                        Intent intent = new Intent(activity, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                });
            }

        } catch (Exception e) {
            Log.e("FooterNavigation", "Error setting up footer: " + e.getMessage());
        }
    }
}
