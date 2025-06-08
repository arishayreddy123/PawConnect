package com.arishay.pawconnect;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.*;

import java.util.*;

public class AdminActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminListingAdapter adapter;
    private List<Listing> listings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminListingAdapter(listings);
        recyclerView.setAdapter(adapter);

        loadAllListings();

        // Setup footer navigation
        FooterNavigation.setupFooterNavigation(this);
    }

    private void loadAllListings() {
        FirebaseFirestore.getInstance().collection("listings")
                .get()
                .addOnSuccessListener(query -> {
                    listings.clear();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        Listing l = doc.toObject(Listing.class);
                        l.id = doc.getId();
                        listings.add(l);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load listings.", Toast.LENGTH_SHORT).show();
                });
    }
}
