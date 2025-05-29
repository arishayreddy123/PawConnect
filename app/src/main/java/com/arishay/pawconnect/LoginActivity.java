package com.arishay.pawconnect;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView goToRegister;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        goToRegister = findViewById(R.id.goToRegister);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loginButton.setOnClickListener(v -> loginUser());

        goToRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Email and password are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        checkUserRole(user.getUid());
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Login failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void checkUserRole(String uid) {
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");

                        if ("admin".equals(role)) {
                            startActivity(new Intent(this, AdminHomeActivity.class));
                            Toast.makeText(this, "Welcome Admin!", Toast.LENGTH_SHORT).show();
                        } else if ("rescuer".equals(role)) {
                            startActivity(new Intent(this, RescuerActivity.class));
                            Toast.makeText(this, "Welcome Rescuer!", Toast.LENGTH_SHORT).show();
                        } else if ("adopter".equals(role)) {
                            startActivity(new Intent(this, AdopterActivity.class));
                            Toast.makeText(this, "Welcome Adopter!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Unknown role. Please contact support.", Toast.LENGTH_LONG).show();
                        }

                        finish(); // Prevent back to login
                    } else {
                        Toast.makeText(this, "User role not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to retrieve user role: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
