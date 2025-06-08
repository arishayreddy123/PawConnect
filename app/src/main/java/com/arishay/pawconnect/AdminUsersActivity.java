package com.arishay.pawconnect;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class AdminUsersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_users);

        // Set up footer navigation
        AdminFooterNavigation.setupFooterNavigation(this);

        // Initialize views
    }
} 