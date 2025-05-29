package com.arishay.pawconnect;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ViewHolder> {

    private List<Listing> listings;

    public ListingAdapter(List<Listing> listings) {
        this.listings = listings;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, breed, age, description;
        Button editButton, deleteButton;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.nameTextView);
            breed = view.findViewById(R.id.breedTextView);
            age = view.findViewById(R.id.ageTextView);
            description = view.findViewById(R.id.descriptionTextView);
            editButton = view.findViewById(R.id.editButton);
            deleteButton = view.findViewById(R.id.deleteButton);
        }
    }

    @Override
    public ListingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listing_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Listing pet = listings.get(position);
        holder.name.setText("Name: " + pet.name);
        holder.breed.setText("Breed: " + pet.breed);
        holder.age.setText("Age: " + pet.age);
        holder.description.setText("Description: " + pet.description);

        holder.deleteButton.setOnClickListener(v -> {
            FirebaseFirestore.getInstance().collection("listings")
                    .document(pet.id)
                    .delete()
                    .addOnSuccessListener(unused -> {
                        listings.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(holder.itemView.getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                    });
        });

        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), EditListingActivity.class);
            intent.putExtra("listingId", pet.id);
            intent.putExtra("name", pet.name);
            intent.putExtra("breed", pet.breed);
            intent.putExtra("age", pet.age);
            intent.putExtra("description", pet.description);
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listings.size();
    }
}
