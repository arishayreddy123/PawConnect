package com.arishay.pawconnect;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.bumptech.glide.Glide;
import java.util.List;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ViewHolder> {

    private List<Listing> listings;

    public ListingAdapter(List<Listing> listings) {
        this.listings = listings;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, breed, age, description;
        Button editButton, deleteButton;
        ImageView petImageView;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.nameTextView);
            breed = view.findViewById(R.id.breedTextView);
            age = view.findViewById(R.id.ageTextView);
            description = view.findViewById(R.id.descriptionTextView);
            editButton = view.findViewById(R.id.editButton);
            deleteButton = view.findViewById(R.id.deleteButton);
            petImageView = view.findViewById(R.id.petImageView);
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
        holder.age.setText("Age: " + (pet.age != null ? pet.age.toString() : ""));
        holder.description.setText("Description: " + pet.description);

        // Load image using Glide
        if (pet.imageUrl != null && !pet.imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                .load(pet.imageUrl)
                .placeholder(android.R.color.darker_gray)
                .error(android.R.drawable.ic_menu_report_image)
                .centerCrop()
                .into(holder.petImageView);
        } else {
            holder.petImageView.setImageResource(android.R.drawable.ic_menu_report_image);
        }

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
