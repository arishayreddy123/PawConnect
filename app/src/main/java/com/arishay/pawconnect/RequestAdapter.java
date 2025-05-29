package com.arishay.pawconnect;

import android.view.*;
import android.widget.*;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.*;
import java.util.*;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    private List<Request> requests;

    public RequestAdapter(List<Request> requests) {
        this.requests = requests;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView listingNameText, adopterEmailText, statusText, messageText;
        Button acceptButton, declineButton;

        public ViewHolder(View view) {
            super(view);
            listingNameText = view.findViewById(R.id.listingNameText);
            adopterEmailText = view.findViewById(R.id.adopterEmailText);
            statusText = view.findViewById(R.id.statusText);
            messageText = view.findViewById(R.id.messageText);
            acceptButton = view.findViewById(R.id.acceptButton);
            declineButton = view.findViewById(R.id.declineButton);
        }
    }

    @Override
    public RequestAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.request_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Request request = requests.get(position);

        holder.statusText.setText("Status: " + request.status);
        holder.messageText.setText("Message: " + (request.message != null ? request.message : "N/A"));

        FirebaseFirestore.getInstance().collection("listings")
                .document(request.listingId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        holder.listingNameText.setText("Pet: " + doc.getString("name"));
                    }
                });

        FirebaseFirestore.getInstance().collection("users")
                .document(request.adopterId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        holder.adopterEmailText.setText("Adopter: " + doc.getString("email"));
                    }
                });

        if (request.status.equals("accepted") || request.status.equals("declined")) {
            holder.acceptButton.setEnabled(false);
            holder.declineButton.setEnabled(false);
            holder.acceptButton.setAlpha(0.5f);
            holder.declineButton.setAlpha(0.5f);
        } else {
            holder.acceptButton.setEnabled(true);
            holder.declineButton.setEnabled(true);
            holder.acceptButton.setAlpha(1f);
            holder.declineButton.setAlpha(1f);
        }

        holder.acceptButton.setOnClickListener(v -> {
            FirebaseFirestore.getInstance().collection("requests")
                    .document(request.id)
                    .update("status", "accepted", "notified", false)
                    .addOnSuccessListener(unused -> {
                        request.status = "accepted";
                        holder.statusText.setText("Status: accepted");
                        holder.acceptButton.setEnabled(false);
                        holder.declineButton.setEnabled(false);
                        holder.acceptButton.setAlpha(0.5f);
                        holder.declineButton.setAlpha(0.5f);
                        Toast.makeText(v.getContext(), "Request accepted", Toast.LENGTH_SHORT).show();
                    });
        });

        holder.declineButton.setOnClickListener(v -> {
            FirebaseFirestore.getInstance().collection("requests")
                    .document(request.id)
                    .update("status", "declined", "notified", false)
                    .addOnSuccessListener(unused -> {
                        request.status = "declined";
                        holder.statusText.setText("Status: declined");
                        holder.acceptButton.setEnabled(false);
                        holder.declineButton.setEnabled(false);
                        holder.acceptButton.setAlpha(0.5f);
                        holder.declineButton.setAlpha(0.5f);
                        Toast.makeText(v.getContext(), "Request declined", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }
}
