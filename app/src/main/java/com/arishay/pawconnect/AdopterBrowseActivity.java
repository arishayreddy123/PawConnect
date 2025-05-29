package com.arishay.pawconnect;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.*;

import java.util.*;

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

    private String adopterId = ""; // Replace with actual adopter ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adopter_browse);

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
    }

    private void fetchListings() {
        FirebaseFirestore.getInstance().collection("listings")
                .get()
                .addOnSuccessListener(query -> {
                    allListings.clear();
                    Set<String> breeds = new HashSet<>();
                    Set<String> ages = new HashSet<>();

                    for (DocumentSnapshot doc : query.getDocuments()) {
                        Listing l = doc.toObject(Listing.class);
                        l.id = doc.getId();
                        allListings.add(l);

                        if (l.breed != null) breeds.add(l.breed);
                        if (l.age != null) ages.add(l.age);
                    }

                    // Sort by newest (timestamp)
                    Collections.sort(allListings, (a, b) -> {
                        Timestamp ta = a.timestamp;
                        Timestamp tb = b.timestamp;
                        if (ta == null || tb == null) return 0;
                        return tb.compareTo(ta); // Newest first
                    });

                    breedOptions.clear();
                    breedOptions.add("All");
                    breedOptions.addAll(breeds);

                    ageOptions.clear();
                    ageOptions.add("All");
                    ageOptions.addAll(ages);

                    breedSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, breedOptions));
                    ageSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, ageOptions));

                    applyFilters();
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
            if (!"All".equals(selectedAge) && (l.age == null || !l.age.equals(selectedAge))) {
                match = false;
            }

            if (match) {
                filteredListings.add(l);
            }
        }

        adapter.notifyDataSetChanged();
    }
}
