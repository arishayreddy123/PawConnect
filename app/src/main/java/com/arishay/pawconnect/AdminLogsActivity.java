package com.arishay.pawconnect;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminLogsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    LogAdapter adapter;
    List<LogEntry> logs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_logs);

        recyclerView = findViewById(R.id.logsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LogAdapter(logs);
        recyclerView.setAdapter(adapter);

        FirebaseFirestore.getInstance().collection("logs")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(query -> {
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        LogEntry entry = doc.toObject(LogEntry.class);
                        logs.add(entry);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    public static class LogEntry {
        public String action;
        public String userId;
        public Date timestamp;

        public LogEntry() {} // Firestore requires empty constructor

        @NonNull
        @Override
        public String toString() {
            String time = timestamp != null
                    ? new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault()).format(timestamp)
                    : "Unknown time";
            return "Action: " + action + "\nUser ID: " + userId + "\nTime: " + time;
        }
    }

    public static class LogAdapter extends RecyclerView.Adapter<LogAdapter.ViewHolder> {
        private List<LogEntry> logs;

        public LogAdapter(List<LogEntry> logs) {
            this.logs = logs;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView logText;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                logText = itemView.findViewById(R.id.logText);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_log, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            LogEntry log = logs.get(position);
            holder.logText.setText(log.toString());
        }

        @Override
        public int getItemCount() {
            return logs.size();
        }
    }
}
