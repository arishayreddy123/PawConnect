package com.arishay.pawconnect;

import com.google.firebase.Timestamp;

public class Listing {
    public String id;
    public String name;
    public String breed;
    public String age;
    public String description;
    public String ownerId;
    public Timestamp timestamp; // ✅ Required for sorting

    public Listing() {
        // Firestore requires a public empty constructor
    }
}
