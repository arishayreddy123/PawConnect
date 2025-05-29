package com.arishay.pawconnect;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.util.*;

public class RescuerRequestsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RequestAdapter adapter;
    List<Request> requestList = new ArrayList<>();
    FirebaseFirestore db;
    String rescuerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rescuer_requests);

        recyclerView = findViewById(R.id.requestsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        rescuerId = FirebaseAuth.getInstance().getUid();

        db.collection("requests")
                .whereEqualTo("rescuerId", rescuerId)
                .get()
                .addOnSuccessListener(query -> {
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        Request r = doc.toObject(Request.class);
                        r.id = doc.getId();
                        requestList.add(r);
                    }
                    adapter = new RequestAdapter(requestList);
                    recyclerView.setAdapter(adapter);
                });
    }
}
