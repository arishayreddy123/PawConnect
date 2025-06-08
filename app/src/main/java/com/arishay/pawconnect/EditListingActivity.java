package com.arishay.pawconnect;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class EditListingActivity extends AppCompatActivity {

    EditText nameEditText, breedEditText, ageEditText, descriptionEditText;
    Button updateButton;

    FirebaseFirestore db;
    String listingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_listing);

        nameEditText = findViewById(R.id.nameEditText);
        breedEditText = findViewById(R.id.breedEditText);
        ageEditText = findViewById(R.id.ageEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        updateButton = findViewById(R.id.updateButton);

        db = FirebaseFirestore.getInstance();

        // Get passed data from intent
        listingId = getIntent().getStringExtra("listingId");
        nameEditText.setText(getIntent().getStringExtra("name"));
        breedEditText.setText(getIntent().getStringExtra("breed"));
        ageEditText.setText(getIntent().getStringExtra("age"));
        descriptionEditText.setText(getIntent().getStringExtra("description"));

        updateButton.setOnClickListener(v -> updateListing());

        FooterNavigation.setupFooterNavigation(this);
    }

    void updateListing() {
        String name = nameEditText.getText().toString().trim();
        String breed = breedEditText.getText().toString().trim();
        String ageStr = ageEditText.getText().toString().trim();
        Long age = null;
        if (!ageStr.isEmpty()) {
            try {
                age = Long.parseLong(ageStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Age must be a number", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        String description = descriptionEditText.getText().toString().trim();

        if (name.isEmpty() || breed.isEmpty() || ageStr.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("name", name);
        updatedData.put("breed", breed);
        updatedData.put("age", age);
        updatedData.put("description", description);

        db.collection("listings").document(listingId)
                .update(updatedData)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Listing updated", Toast.LENGTH_SHORT).show();
                    finish(); // Return to previous screen
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
