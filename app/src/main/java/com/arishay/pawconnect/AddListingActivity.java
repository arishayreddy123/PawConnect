package com.arishay.pawconnect;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.HashMap;
import java.util.Map;

public class AddListingActivity extends AppCompatActivity {

    private EditText nameInput, breedInput, ageInput, descriptionInput;
    private Button saveButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_listing);

        nameInput = findViewById(R.id.nameInput);
        breedInput = findViewById(R.id.breedInput);
        ageInput = findViewById(R.id.ageInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        saveButton = findViewById(R.id.saveButton);

        db = FirebaseFirestore.getInstance();

        saveButton.setOnClickListener(v -> saveListing());
    }

    private void saveListing() {
        String name = nameInput.getText().toString().trim();
        String breed = breedInput.getText().toString().trim();
        String age = ageInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String ownerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (name.isEmpty() || breed.isEmpty() || age.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> listing = new HashMap<>();
        listing.put("name", name);
        listing.put("breed", breed);
        listing.put("age", age);
        listing.put("description", description);
        listing.put("ownerId", ownerId);
        listing.put("timestamp", FieldValue.serverTimestamp()); // ✅ Required for sorting

        db.collection("listings").add(listing)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(this, "Pet listing added successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
