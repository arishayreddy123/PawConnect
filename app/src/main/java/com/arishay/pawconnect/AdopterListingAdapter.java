package com.arishay.pawconnect;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.*;
import android.widget.*;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.*;
import java.util.*;

public class AdopterListingAdapter extends RecyclerView.Adapter<AdopterListingAdapter.ViewHolder> {

    private List<Listing> listings;
    private String adopterId;
    private FirebaseFirestore db;

    public AdopterListingAdapter(List<Listing> listings, String adopterId) {
        this.listings = listings;
        this.adopterId = adopterId;
        this.db = FirebaseFirestore.getInstance();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, breed, age, description, statusText;
        Button sendRequestButton;
        ImageButton favoriteButton;
        ImageView petImageView;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.nameTextView);
            breed = view.findViewById(R.id.breedTextView);
            age = view.findViewById(R.id.ageTextView);
            description = view.findViewById(R.id.descriptionTextView);
            sendRequestButton = view.findViewById(R.id.editButton);
            favoriteButton = view.findViewById(R.id.favoriteButton);
            statusText = new TextView(view.getContext());
            ((ViewGroup) view).addView(statusText);
            view.findViewById(R.id.deleteButton).setVisibility(View.GONE);
            petImageView = view.findViewById(R.id.petImageView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listing_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Listing pet = listings.get(position);
        Context context = holder.itemView.getContext();

        holder.name.setText("Name: " + pet.name);
        holder.breed.setText("Breed: " + pet.breed);
        holder.age.setText("Age: " + (pet.age != null ? pet.age.toString() : ""));
        holder.description.setText("Description: " + pet.description);
        holder.statusText.setText("");

        DocumentReference favRef = db.collection("users")
                .document(adopterId)
                .collection("favourites")
                .document(pet.id);

        favRef.get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                holder.favoriteButton.setImageResource(android.R.drawable.btn_star_big_on);
            } else {
                holder.favoriteButton.setImageResource(android.R.drawable.btn_star_big_off);
            }
        });

        holder.favoriteButton.setOnClickListener(v -> {
            favRef.get().addOnSuccessListener(doc -> {
                if (doc.exists()) {
                    favRef.delete();
                    holder.favoriteButton.setImageResource(android.R.drawable.btn_star_big_off);
                    Toast.makeText(context, "Removed from favourites", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, Object> fav = new HashMap<>();
                    fav.put("timestamp", FieldValue.serverTimestamp());
                    favRef.set(fav);
                    holder.favoriteButton.setImageResource(android.R.drawable.btn_star_big_on);
                    Toast.makeText(context, "Added to favourites", Toast.LENGTH_SHORT).show();
                }
            });
        });

        db.collection("requests")
                .whereEqualTo("listingId", pet.id)
                .whereEqualTo("adopterId", adopterId)
                .limit(1)
                .get()
                .addOnSuccessListener(query -> {
                    if (!query.isEmpty()) {
                        DocumentSnapshot doc = query.getDocuments().get(0);
                        String status = doc.getString("status");
                        holder.statusText.setText("Status: " + status);

                        if ("pending".equals(status)) {
                            holder.sendRequestButton.setText("Cancel Request");
                            holder.sendRequestButton.setEnabled(true);
                            holder.sendRequestButton.setAlpha(1f);

                            holder.sendRequestButton.setOnClickListener(v -> {
                                doc.getReference().delete()
                                        .addOnSuccessListener(unused -> {
                                            Toast.makeText(context, "Request cancelled", Toast.LENGTH_SHORT).show();
                                            holder.statusText.setText("No request yet");
                                            holder.sendRequestButton.setText("Send Request");
                                        })
                                        .addOnFailureListener(e ->
                                                Toast.makeText(context, "Failed to cancel", Toast.LENGTH_SHORT).show());
                            });

                        } else if ("declined".equals(status)) {
                            holder.sendRequestButton.setText("Resend Request");
                            holder.sendRequestButton.setEnabled(true);
                            holder.sendRequestButton.setAlpha(1f);

                            holder.sendRequestButton.setOnClickListener(v -> {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("Resend Request - Why do you want to adopt this pet?");
                                final EditText input = new EditText(context);
                                input.setHint("Type your reason...");
                                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                                builder.setView(input);

                                builder.setPositiveButton("Send", (dialog, which) -> {
                                    String message = input.getText().toString().trim();
                                    if (message.isEmpty()) {
                                        Toast.makeText(context, "Message required!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    Map<String, Object> update = new HashMap<>();
                                    update.put("status", "pending");
                                    update.put("message", message);
                                    update.put("notified", false);

                                    doc.getReference().update(update)
                                            .addOnSuccessListener(unused -> {
                                                Toast.makeText(context, "Request resent!", Toast.LENGTH_SHORT).show();
                                                holder.statusText.setText("Status: pending");
                                                holder.sendRequestButton.setText("Cancel Request");
                                            })
                                            .addOnFailureListener(e ->
                                                    Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                                });

                                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
                                builder.show();
                            });

                        } else {
                            holder.sendRequestButton.setEnabled(false);
                            holder.sendRequestButton.setAlpha(0.5f);
                        }

                    } else {
                        holder.statusText.setText("No request yet");
                        holder.sendRequestButton.setText("Send Request");
                        holder.sendRequestButton.setEnabled(true);
                        holder.sendRequestButton.setAlpha(1f);

                        holder.sendRequestButton.setOnClickListener(v -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Why do you want to adopt this pet?");
                            final EditText input = new EditText(context);
                            input.setHint("Type your reason here...");
                            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                            builder.setView(input);

                            builder.setPositiveButton("Send", (dialog, which) -> {
                                String message = input.getText().toString().trim();
                                if (message.isEmpty()) {
                                    Toast.makeText(context, "Message is required!", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                Map<String, Object> request = new HashMap<>();
                                request.put("listingId", pet.id);
                                request.put("rescuerId", pet.ownerId);
                                request.put("adopterId", adopterId);
                                request.put("status", "pending");
                                request.put("message", message);
                                request.put("notified", false);

                                db.collection("requests").add(request)
                                        .addOnSuccessListener(unused -> {
                                            Toast.makeText(context, "Request sent!", Toast.LENGTH_SHORT).show();
                                            holder.statusText.setText("Status: pending");
                                            holder.sendRequestButton.setText("Cancel Request");
                                        })
                                        .addOnFailureListener(e ->
                                                Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            });

                            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
                            builder.show();
                        });
                    }
                });

        if (pet.imageUrl != null && !pet.imageUrl.isEmpty()) {
            com.bumptech.glide.Glide.with(context)
                .load(pet.imageUrl)
                .placeholder(android.R.color.darker_gray)
                .error(android.R.drawable.ic_menu_report_image)
                .centerCrop()
                .into(holder.petImageView);
        } else {
            holder.petImageView.setImageResource(android.R.drawable.ic_menu_report_image);
        }
    }

    @Override
    public int getItemCount() {
        return listings.size();
    }
}
