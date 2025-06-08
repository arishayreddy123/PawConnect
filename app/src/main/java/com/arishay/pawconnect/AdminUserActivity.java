package com.arishay.pawconnect;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.*;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.*;

import java.util.*;

public class AdminUserActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<UserModel> users = new ArrayList<>();
    private List<UserModel> allUsers = new ArrayList<>();
    private FirebaseFirestore db;

    private EditText searchEmailInput;
    private Spinner roleFilterSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_users);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        db = FirebaseFirestore.getInstance();

        adapter = new UserAdapter(users, db);
        recyclerView.setAdapter(adapter);

        searchEmailInput = findViewById(R.id.searchEmailInput);
        roleFilterSpinner = findViewById(R.id.roleFilterSpinner);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"All", "admin", "rescuer", "adopter"});
        roleFilterSpinner.setAdapter(spinnerAdapter);

        searchEmailInput.addTextChangedListener(new TextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyUserFilter();
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        roleFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyUserFilter();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        loadUsers();

        // Setup footer navigation
        FooterNavigation.setupFooterNavigation(this);
    }

    private void loadUsers() {
        db.collection("users").get()
                .addOnSuccessListener(query -> {
                    allUsers.clear();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        UserModel u = doc.toObject(UserModel.class);
                        if (u != null) {
                            u.uid = doc.getId();
                            allUsers.add(u);
                        }
                    }
                    applyUserFilter();
                });
    }

    private void applyUserFilter() {
        String emailQuery = searchEmailInput.getText().toString().toLowerCase();
        String roleFilter = roleFilterSpinner.getSelectedItem().toString();

        users.clear();
        for (UserModel u : allUsers) {
            boolean match = true;

            if (!TextUtils.isEmpty(emailQuery) && (u.email == null || !u.email.toLowerCase().contains(emailQuery))) {
                match = false;
            }

            if (!"All".equals(roleFilter) && (u.role == null || !u.role.equals(roleFilter))) {
                match = false;
            }

            if (match) users.add(u);
        }

        adapter.notifyDataSetChanged();
    }

    public static class UserModel {
        public String uid, email, role;
    }

    public static class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

        private final List<UserModel> users;
        private final FirebaseFirestore db;

        public UserAdapter(List<UserModel> users, FirebaseFirestore db) {
            this.users = users;
            this.db = db;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView email, role;
            Button delete, update;

            public ViewHolder(View view) {
                super(view);
                email = view.findViewById(R.id.userEmail);
                role = view.findViewById(R.id.userRole);
                delete = view.findViewById(R.id.deleteUserBtn);
                update = view.findViewById(R.id.updateUserBtn);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            UserModel user = users.get(position);
            holder.email.setText(user.email);
            holder.role.setText("Role: " + user.role);

            holder.delete.setOnClickListener(v -> {
                db.collection("users").document(user.uid).delete()
                        .addOnSuccessListener(aVoid -> {
                            users.remove(position);
                            notifyItemRemoved(position);

                            Map<String, Object> log = new HashMap<>();
                            log.put("action", "delete_user");
                            log.put("userId", user.uid);
                            log.put("timestamp", FieldValue.serverTimestamp());
                            db.collection("logs").add(log);
                        });
            });

            holder.update.setOnClickListener(v -> {
                Context context = v.getContext();
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setPadding(20, 20, 20, 20);

                EditText emailField = new EditText(context);
                emailField.setHint("Email");
                emailField.setText(user.email);
                layout.addView(emailField);

                EditText nameField = new EditText(context);
                nameField.setHint("Name");
                layout.addView(nameField);

                Spinner roleSpinner = new Spinner(context);
                ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item,
                        new String[]{"admin", "rescuer", "adopter"});
                roleSpinner.setAdapter(roleAdapter);
                int selectedIndex = Arrays.asList("admin", "rescuer", "adopter").indexOf(user.role);
                roleSpinner.setSelection(selectedIndex);
                layout.addView(roleSpinner);

                new AlertDialog.Builder(context)
                        .setTitle("Update User Info")
                        .setView(layout)
                        .setPositiveButton("Update", (dialog, which) -> {
                            String newEmail = emailField.getText().toString().trim();
                            String newName = nameField.getText().toString().trim();
                            String newRole = roleSpinner.getSelectedItem().toString();

                            Map<String, Object> updated = new HashMap<>();
                            updated.put("email", newEmail);
                            updated.put("name", newName);
                            updated.put("role", newRole);

                            db.collection("users").document(user.uid)
                                    .update(updated)
                                    .addOnSuccessListener(aVoid -> {
                                        user.email = newEmail;
                                        user.role = newRole;
                                        notifyItemChanged(position);

                                        Map<String, Object> log = new HashMap<>();
                                        log.put("action", "update_user");
                                        log.put("userId", user.uid);
                                        log.put("timestamp", FieldValue.serverTimestamp());
                                        db.collection("logs").add(log);
                                    });
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        }

        @Override
        public int getItemCount() {
            return users.size();
        }
    }
}
