package com.arishay.pawconnect;

import android.os.Bundle;
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

        // Load listings owned by the current user from ListingManager (in-memory singleton)
        // This avoids any use of Firestore or external DBs.
        // Only listings created during this app session by this user will be shown.
        String currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().getUid();
        listingList.clear();
        listingList.addAll(ListingManager.getInstance().getListingsByOwner(currentUserId));
        adapter.notifyDataSetChanged();


        FooterNavigation.setupFooterNavigation(this);
    }
}
