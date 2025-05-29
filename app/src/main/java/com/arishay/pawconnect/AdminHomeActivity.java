package com.arishay.pawconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AdminHomeActivity extends AppCompatActivity {

    Button manageListingsBtn, manageUsersBtn, addUserBtn, dashboardBtn, viewLogsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        manageListingsBtn = findViewById(R.id.manageListingsBtn);
        manageUsersBtn = findViewById(R.id.manageUsersBtn);
        addUserBtn = findViewById(R.id.addUserBtn);
        dashboardBtn = findViewById(R.id.dashboardBtn);
        viewLogsBtn = findViewById(R.id.viewLogsBtn);

        manageListingsBtn.setOnClickListener(v ->
                startActivity(new Intent(this, AdminActivity.class)));

        manageUsersBtn.setOnClickListener(v ->
                startActivity(new Intent(this, AdminUserActivity.class)));

        addUserBtn.setOnClickListener(v ->
                startActivity(new Intent(this, AdminAddUserActivity.class)));

        dashboardBtn.setOnClickListener(v ->
                startActivity(new Intent(this, AdminDashboardActivity.class)));

        viewLogsBtn.setOnClickListener(v ->
                startActivity(new Intent(this, AdminLogsActivity.class)));  // make sure this activity exists
    }
}
