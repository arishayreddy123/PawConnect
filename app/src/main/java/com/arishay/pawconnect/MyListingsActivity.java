package com.arishay.pawconnect;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MyListingsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ListingAdapter adapter;
    List<Listing> listingList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_listings);

        recyclerView = findViewById(R.id.listingsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ListingAdapter(listingList);
        recyclerView.setAdapter(adapter);

        String currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().getUid();
        com.google.firebase.firestore.FirebaseFirestore.getInstance()
            .collection("listings")
            .whereEqualTo("ownerId", currentUserId)
            .get()
            .addOnSuccessListener(querySnapshot -> {
                listingList.clear();
                for (com.google.firebase.firestore.DocumentSnapshot doc : querySnapshot.getDocuments()) {
                    Listing l = doc.toObject(Listing.class);
                    l.id = doc.getId();
                    listingList.add(l);
                }
                adapter.notifyDataSetChanged();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to load your listings.", Toast.LENGTH_SHORT).show();
            });

        FooterNavigation.setupFooterNavigation(this);
    }
}
