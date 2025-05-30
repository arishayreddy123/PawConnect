package com.arishay.pawconnect;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class to manage pet listings in memory (no Firestore).
 */
public class ListingManager {
    private static ListingManager instance;
    private final List<Listing> listings;

    private ListingManager() {
        listings = new ArrayList<>();
    }

    public static ListingManager getInstance() {
        if (instance == null) {
            instance = new ListingManager();
        }
        return instance;
    }

    // Add a new listing
    public void addListing(Listing listing) {
        listings.add(0, listing); // add to top (newest first)
    }

    // Get all listings
    public List<Listing> getAllListings() {
        return new ArrayList<>(listings);
    }

    // Get listings by ownerId
    public List<Listing> getListingsByOwner(String ownerId) {
        List<Listing> result = new ArrayList<>();
        for (Listing l : listings) {
            if (l.ownerId != null && l.ownerId.equals(ownerId)) {
                result.add(l);
            }
        }
        return result;
    }
} 