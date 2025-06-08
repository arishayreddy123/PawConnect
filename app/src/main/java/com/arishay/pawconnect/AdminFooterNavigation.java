package com.arishay.pawconnect;

import android.content.Intent;
import android.app.Activity;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;

public class AdminFooterNavigation {
    public static void setupFooterNavigation(Activity activity) {
        Button backButton = activity.findViewById(R.id.backButton);
        Button logoutButton = activity.findViewById(R.id.logoutButton);

        backButton.setOnClickListener(v -> activity.finish());

        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(activity, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
            activity.finish();
        });
    }
} 