package com.arishay.pawconnect;

import android.app.AlertDialog;
import android.content.Context;
import android.view.*;
import android.widget.*;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.bumptech.glide.Glide;

import java.util.List;

public class AdminListingAdapter extends RecyclerView.Adapter<AdminListingAdapter.ViewHolder> {

    private List<Listing> listings;

    public AdminListingAdapter(List<Listing> listings) {
        this.listings = listings;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, breed, age, description;
        Button deleteButton;
        ImageView petImageView;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.nameTextView);
            breed = view.findViewById(R.id.breedTextView);
            age = view.findViewById(R.id.ageTextView);
            description = view.findViewById(R.id.descriptionTextView);
            deleteButton = view.findViewById(R.id.deleteButton);
            petImageView = view.findViewById(R.id.petImageView);
            view.findViewById(R.id.editButton).setVisibility(View.GONE); // Hide Edit
        }
    }

    @Override
    public AdminListingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listing_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdminListingAdapter.ViewHolder holder, int position) {
        Listing pet = listings.get(position);
        holder.name.setText("Name: " + pet.name);
        holder.breed.setText("Breed: " + pet.breed);
        holder.age.setText("Age: " + (pet.age != null ? pet.age.toString() : ""));
        holder.description.setText("Description: " + pet.description);

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

        holder.deleteButton.setVisibility(View.VISIBLE);
        holder.deleteButton.setOnClickListener(v -> {
            Context context = v.getContext();

            new AlertDialog.Builder(context)
                    .setTitle("Delete Listing")
                    .setMessage("Are you sure you want to delete this listing?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        FirebaseFirestore.getInstance().collection("listings")
                                .document(pet.id)
                                .delete()
                                .addOnSuccessListener(unused -> {
                                    listings.remove(position);
                                    notifyItemRemoved(position);
                                    Toast.makeText(context, "Listing deleted", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show());
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return listings.size();
    }
}
