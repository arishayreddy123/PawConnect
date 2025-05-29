package com.arishay.pawconnect.api;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Interface for Imgur API endpoints
 */
public interface ImgurApi {
    @Multipart
    @POST("3/image")
    Call<ImgurResponse> uploadImage(
        @Header("Authorization") String clientId,
        @Part MultipartBody.Part image
    );

    /**
     * Response model for Imgur API
     */
    class ImgurResponse {
        public boolean success;
        public int status;
        public Data data;

        public static class Data {
            public String id;
            public String title;
            public String description;
            public String link;  // This is the URL we need
            public String deletehash;
            public int datetime;
        }
    }
} 