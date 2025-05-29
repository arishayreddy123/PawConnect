package com.arishay.pawconnect;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.util.*;

public class AdopterRequestsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    AdopterRequestAdapter adapter;
    List<Request> requestList = new ArrayList<>();
    FirebaseFirestore db;
    String adopterId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adopter_requests);

        recyclerView = findViewById(R.id.myRequestsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adopterId = FirebaseAuth.getInstance().getUid();
        db = FirebaseFirestore.getInstance();

        db.collection("requests")
                .whereEqualTo("adopterId", adopterId)
                .get()
                .addOnSuccessListener(query -> {
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        Request r = doc.toObject(Request.class);
                        r.id = doc.getId();
                        requestList.add(r);
                    }
                    adapter = new AdopterRequestAdapter(requestList);
                    recyclerView.setAdapter(adapter);
                });
    }
}
