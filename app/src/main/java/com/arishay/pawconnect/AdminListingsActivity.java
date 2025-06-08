package com.arishay.pawconnect;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class AdminListingsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AdminListingAdapter adapter;
    private List<Listing> listings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_listings);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminListingAdapter(listings);
        recyclerView.setAdapter(adapter);

        loadListings();

        // Set up footer navigation
        AdminFooterNavigation.setupFooterNavigation(this);
    }

    private void loadListings() {
        FirebaseFirestore.getInstance().collection("listings")
            .get()
            .addOnSuccessListener(querySnapshot -> {
                listings.clear();
                for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                    Listing l = doc.toObject(Listing.class);
                    if (l != null) {
                        l.id = doc.getId();
                        listings.add(l);
                    }
                }
                adapter.notifyDataSetChanged();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to load listings", Toast.LENGTH_SHORT).show();
            });
    }
} 