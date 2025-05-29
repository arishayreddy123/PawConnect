package com.arishay.pawconnect;

import android.view.*;
import android.widget.*;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.*;
import java.util.*;

public class AdopterRequestAdapter extends RecyclerView.Adapter<AdopterRequestAdapter.ViewHolder> {

    private List<Request> requests;

    public AdopterRequestAdapter(List<Request> requests) {
        this.requests = requests;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView petNameText, statusText, messageText;
        Button cancelRequestButton;

        public ViewHolder(View view) {
            super(view);
            petNameText = view.findViewById(R.id.petNameText);
            statusText = view.findViewById(R.id.statusText);
            messageText = view.findViewById(R.id.messageText);
            cancelRequestButton = view.findViewById(R.id.cancelRequestButton);
        }
    }

    @Override
    public AdopterRequestAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.request_item_adopter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Request request = requests.get(position);

        holder.statusText.setText("Status: " + request.status);
        holder.messageText.setText("Message: " + request.message);

        // Load pet name
        FirebaseFirestore.getInstance().collection("listings")
                .document(request.listingId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        holder.petNameText.setText("Pet: " + doc.getString("name"));
                    }
                });

        // Hide cancel button if already accepted/declined
        if (!request.status.equals("pending")) {
            holder.cancelRequestButton.setEnabled(false);
            holder.cancelRequestButton.setAlpha(0.5f);
        }

        holder.cancelRequestButton.setOnClickListener(v -> {
            FirebaseFirestore.getInstance().collection("requests")
                    .document(request.id)
                    .delete()
                    .addOnSuccessListener(unused -> {
                        requests.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(v.getContext(), "Request canceled", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }
}
