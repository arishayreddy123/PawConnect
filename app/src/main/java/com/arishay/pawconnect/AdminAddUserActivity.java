package com.arishay.pawconnect;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.util.HashMap;
import java.util.Map;

public class AdminAddUserActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput, nameInput;
    private Spinner roleSpinner;
    private Button addUserBtn;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_user);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        nameInput = findViewById(R.id.nameInput);
        roleSpinner = findViewById(R.id.roleSpinner);
        addUserBtn = findViewById(R.id.addUserBtn);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"admin", "rescuer", "adopter"});
        roleSpinner.setAdapter(adapter);

        addUserBtn.setOnClickListener(v -> createUser());
    }

    private void createUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String name = nameInput.getText().toString().trim();
        String role = roleSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.fetchSignInMethodsForEmail(email)
                .addOnSuccessListener(result -> {
                    if (result.getSignInMethods() != null && !result.getSignInMethods().isEmpty()) {
                        Toast.makeText(this, "Email already exists!", Toast.LENGTH_SHORT).show();
                    } else {
                        auth.createUserWithEmailAndPassword(email, password)
                                .addOnSuccessListener(authResult -> {
                                    FirebaseUser firebaseUser = authResult.getUser();
                                    if (firebaseUser != null) {
                                        String uid = firebaseUser.getUid();
                                        Map<String, Object> userMap = new HashMap<>();
                                        userMap.put("email", email);
                                        userMap.put("name", name);
                                        userMap.put("role", role);

                                        db.collection("users").document(uid)
                                                .set(userMap)
                                                .addOnSuccessListener(aVoid ->
                                                        Toast.makeText(this, "User created!", Toast.LENGTH_SHORT).show())
                                                .addOnFailureListener(e ->
                                                        Toast.makeText(this, "Firestore error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                                    }
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Auth error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error checking email: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
