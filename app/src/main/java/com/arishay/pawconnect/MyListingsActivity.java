package com.arishay.pawconnect;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.util.ArrayList;
import java.util.List;

public class MyListingsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ListingAdapter adapter;
    List<Listing> listingList = new ArrayList<>();
    FirebaseFirestore db;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_listings);

        recyclerView = findViewById(R.id.listingsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ListingAdapter(listingList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getUid();

        db.collection("listings")
                .whereEqualTo("ownerId", currentUserId)
                .get()
                .addOnSuccessListener(query -> {
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        Listing pet = doc.toObject(Listing.class);
                        pet.id = doc.getId();
                        listingList.add(pet);
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}
