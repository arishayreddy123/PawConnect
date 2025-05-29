package com.arishay.pawconnect;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.arishay.pawconnect.api.ImgurApi;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddListingActivity extends AppCompatActivity {
    private static final String IMGUR_CLIENT_ID = "YOUR_CLIENT_ID"; // Replace with your Imgur Client ID
    private static final int PERMISSION_REQUEST_CODE = 1001;

    private EditText nameInput, breedInput, ageInput, descriptionInput;
    private Button saveButton, selectImageButton;
    private ImageView imagePreview;
    private FirebaseFirestore db;
    private Uri selectedImageUri;
    private String uploadedImageUrl;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                selectedImageUri = result.getData().getData();
                if (selectedImageUri != null) {
                    // Display selected image
                    Glide.with(this)
                        .load(selectedImageUri)
                        .centerCrop()
                        .into(imagePreview);

                    // Show AlertDialog before uploading the image to Imgur
                    new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Notice")
                        .setMessage("The image will be uploaded to an external server (Imgur). Make sure it doesn't contain personal information.")
                        .setPositiveButton("OK", (dialog, which) -> uploadImageToImgur(selectedImageUri))
                        .setNegativeButton("Cancel", null)
                        .show();
                }
            }
        }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_listing);

        // Initialize views
        nameInput = findViewById(R.id.nameInput);
        breedInput = findViewById(R.id.breedInput);
        ageInput = findViewById(R.id.ageInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        saveButton = findViewById(R.id.saveButton);
        selectImageButton = findViewById(R.id.selectImageButton);
        imagePreview = findViewById(R.id.imagePreview);

        db = FirebaseFirestore.getInstance();

        // Set up image selection
        selectImageButton.setOnClickListener(v -> checkPermissionAndPickImage());
        saveButton.setOnClickListener(v -> saveListing());
    }

    private void checkPermissionAndPickImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
        } else {
            openImagePicker();
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(this, "Permission required to select image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImageToImgur(Uri imageUri) {
        try {
            // Create a temporary file from the URI
            File imageFile = createTempFileFromUri(imageUri);
            
            // Create Retrofit instance
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

            Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.imgur.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

            ImgurApi imgurApi = retrofit.create(ImgurApi.class);

            // Create multipart request
            RequestBody requestFile = RequestBody.create(
                MediaType.parse("image/*"),
                imageFile
            );
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);

            // Make the API call
            Call<ImgurApi.ImgurResponse> call = imgurApi.uploadImage("Client-ID " + IMGUR_CLIENT_ID, body);
            call.enqueue(new Callback<ImgurApi.ImgurResponse>() {
                @Override
                public void onResponse(@NonNull Call<ImgurApi.ImgurResponse> call,
                                     @NonNull Response<ImgurApi.ImgurResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().success) {
                        uploadedImageUrl = response.body().data.link;
                        runOnUiThread(() -> 
                            Toast.makeText(AddListingActivity.this,
                                "Image uploaded successfully!", Toast.LENGTH_SHORT).show()
                        );
                    } else {
                        runOnUiThread(() -> 
                            Toast.makeText(AddListingActivity.this,
                                "Failed to upload image", Toast.LENGTH_SHORT).show()
                        );
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ImgurApi.ImgurResponse> call, @NonNull Throwable t) {
                    runOnUiThread(() -> 
                        Toast.makeText(AddListingActivity.this,
                            "Error: " + t.getMessage(), Toast.LENGTH_LONG).show()
                    );
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "Error preparing image: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private File createTempFileFromUri(Uri uri) throws Exception {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        File tempFile = File.createTempFile("imgur_upload", ".jpg", getCacheDir());
        FileOutputStream outputStream = new FileOutputStream(tempFile);
        
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        
        outputStream.close();
        inputStream.close();
        return tempFile;
    }

    private void saveListing() {
        String name = nameInput.getText().toString().trim();
        String breed = breedInput.getText().toString().trim();
        String age = ageInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String ownerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (name.isEmpty() || breed.isEmpty() || age.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> listing = new HashMap<>();
        listing.put("name", name);
        listing.put("breed", breed);
        listing.put("age", age);
        listing.put("description", description);
        listing.put("ownerId", ownerId);
        listing.put("timestamp", FieldValue.serverTimestamp());
        
        // Add image URL if available
        if (uploadedImageUrl != null) {
            listing.put("imageUrl", uploadedImageUrl);
        }

        db.collection("listings").add(listing)
            .addOnSuccessListener(docRef -> {
                Toast.makeText(this, "Pet listing added successfully!", Toast.LENGTH_SHORT).show();
                finish();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
    }
}
