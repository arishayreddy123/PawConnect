package com.arishay.pawconnect;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.*;

public class UserProfileActivity extends AppCompatActivity {

    EditText nameEditText, emailEditText, passwordEditText;
    Button updateButton, forgotPasswordButton;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        updateButton = findViewById(R.id.updateButton);
        forgotPasswordButton = findViewById(R.id.forgotPasswordButton);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user != null) {
            nameEditText.setText(user.getDisplayName());
            emailEditText.setText(user.getEmail());
        }

        updateButton.setOnClickListener(v -> updateProfile());
        forgotPasswordButton.setOnClickListener(v -> sendPasswordResetEmail());

        FooterNavigation.setupFooterNavigation(this);
    }

    private void updateProfile() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Name and Email cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (user != null) {
            // Re-authenticate if password provided
            if (!TextUtils.isEmpty(password)) {
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
                user.reauthenticate(credential).addOnSuccessListener(task -> {
                    updateNameAndEmail(name, email);
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Re-auth failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } else {
                updateNameAndEmail(name, email);
            }
        }
    }

    private void updateNameAndEmail(String name, String email) {
        user.updateEmail(email).addOnSuccessListener(unused -> {
            user.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(name).build())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Email update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void sendPasswordResetEmail() {
        String email = user.getEmail();
        if (!TextUtils.isEmpty(email)) {
            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Reset email sent to " + email, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to send reset email", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
