package com.arishay.pawconnect;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.util.*;

public class MyFavouritesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdopterListingAdapter adapter;
    private List<Listing> listingList = new ArrayList<>();
    private FirebaseFirestore db;
    private String adopterId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favourites);

        recyclerView = findViewById(R.id.recyclerViewFavourites);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdopterListingAdapter(listingList, FirebaseAuth.getInstance().getCurrentUser().getUid());
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        adopterId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadFavourites();

        FooterNavigation.setupFooterNavigation(this);
    }

    private void loadFavourites() {
        db.collection("users")
                .document(adopterId)
                .collection("favourites")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<String> favIds = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        favIds.add(doc.getId());
                    }

                    if (favIds.isEmpty()) {
                        Toast.makeText(this, "No favourites yet.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    db.collection("listings")
                            .whereIn(FieldPath.documentId(), favIds)
                            .get()
                            .addOnSuccessListener(query -> {
                                listingList.clear();
                                for (DocumentSnapshot doc : query.getDocuments()) {
                                    Listing pet = doc.toObject(Listing.class);
                                    pet.id = doc.getId();
                                    listingList.add(pet);
                                }
                                adapter.notifyDataSetChanged();
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Failed to load listings", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load favourites", Toast.LENGTH_SHORT).show());
    }
}
