package com.arishay.pawconnect;

import java.util.ArrayList;
import java.util.List;

public class ListingManager {
    private static ListingManager instance;
    private List<Listing> allListings;

    private ListingManager() {
        allListings = new ArrayList<>();
        // You can add default dummy listings here or load from Firebase
    }

    public static ListingManager getInstance() {
        if (instance == null) {
            instance = new ListingManager();
        }
        return instance;
    }

    public List<Listing> getAllListings() {
        return allListings;
    }

    public void addListing(Listing listing) {
        allListings.add(listing);
    }

    public void clearListings() {
        allListings.clear();
    }

    public List<Listing> getListingsByOwner(String ownerId) {
        List<Listing> filtered = new ArrayList<>();
        for (Listing listing : allListings) {
            if (listing.ownerId != null && listing.ownerId.equals(ownerId)) {
                filtered.add(listing);
            }
        }
        return filtered;
    }

}
