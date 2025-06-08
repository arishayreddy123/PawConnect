package com.arishay.pawconnect;

import com.google.firebase.Timestamp;

public class Listing {
    public String id;
    public String name;
    public String breed;
    public Long age;
    public String description;
    public String ownerId;
    public Timestamp timestamp;
    public String imageUrl;


    // URL of the pet image (uploaded to Imgur or other storage)
//    public String imageUrl;

    public Listing() {
        // Firestore requires a public empty constructor
    }
}
