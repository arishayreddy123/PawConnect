package com.arishay.pawconnect;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.util.*;
import com.arishay.pawconnect.ListingManager;


public class AdopterBrowseActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdopterListingAdapter adapter;
    private List<Listing> allListings = new ArrayList<>();
    private List<Listing> filteredListings = new ArrayList<>();

    private EditText searchInput;
    private Spinner breedSpinner, ageSpinner;
    private Button clearFiltersButton;

    private List<String> breedOptions = new ArrayList<>();
    private List<String> ageOptions = new ArrayList<>();

    // This variable will store the unique ID of the currently logged-in adopter (user).
    // It is assigned in onCreate() after verifying that a user is authenticated with Firebase.
    private String adopterId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adopter_browse);

        // Retrieve the currently authenticated user from Firebase Authentication.
        // If no user is logged in, show a message and close the activity to prevent unauthorized access.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // Assign the unique user ID (UID) of the authenticated user to adopterId.
        // This UID is used throughout the activity to identify the adopter in Firestore queries and adapter logic.
        adopterId = currentUser.getUid();

        recyclerView = findViewById(R.id.recyclerView);
        searchInput = findViewById(R.id.searchInput);
        breedSpinner = findViewById(R.id.breedSpinner);
        ageSpinner = findViewById(R.id.ageSpinner);
        clearFiltersButton = findViewById(R.id.clearFiltersButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdopterListingAdapter(filteredListings, adopterId);
        recyclerView.setAdapter(adapter);

        fetchListings();

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        breedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                applyFilters();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        ageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                applyFilters();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        clearFiltersButton.setOnClickListener(v -> {
            searchInput.setText("");
            if (!breedOptions.isEmpty()) breedSpinner.setSelection(0);
            if (!ageOptions.isEmpty()) ageSpinner.setSelection(0);
        });

        FooterNavigation.setupFooterNavigation(this);
    }

    private void fetchListings() {
        // Load all listings from Firestore
        allListings.clear();
        FirebaseFirestore.getInstance().collection("listings")
            .get()
            .addOnSuccessListener(querySnapshot -> {
                for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                    Listing l = doc.toObject(Listing.class);
                    if (l != null) {
                        l.id = doc.getId(); // Set the Firestore document ID
                        allListings.add(l);
                    }
                }
                // Collect unique breed and age options for filtering
                Set<String> breeds = new HashSet<>();
                Set<String> ages = new HashSet<>();
                for (Listing l : allListings) {
                    if (l.breed != null) breeds.add(l.breed);
                    if (l.age != null) ages.add(l.age.toString());
                }
                breedOptions.clear();
                breedOptions.add("All");
                breedOptions.addAll(breeds);
                ageOptions.clear();
                ageOptions.add("All");
                ageOptions.addAll(ages);
                breedSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, breedOptions));
                ageSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, ageOptions));
                applyFilters();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to load listings from Firestore.", Toast.LENGTH_SHORT).show();
            });
    }

    private void applyFilters() {
        String searchText = searchInput.getText().toString().toLowerCase();
        String selectedBreed = breedSpinner.getSelectedItem().toString();
        String selectedAge = ageSpinner.getSelectedItem().toString();

        filteredListings.clear();

        for (Listing l : allListings) {
            boolean match = true;

            if (!searchText.isEmpty() && (l.name == null || !l.name.toLowerCase().contains(searchText))) {
                match = false;
            }
            if (!"All".equals(selectedBreed) && (l.breed == null || !l.breed.equals(selectedBreed))) {
                match = false;
            }
            if (!"All".equals(selectedAge) && (l.age == null || !selectedAge.equals(l.age.toString()))) {
                match = false;
            }

            if (match) {
                filteredListings.add(l);
            }
        }

        adapter.notifyDataSetChanged();
    }
}
