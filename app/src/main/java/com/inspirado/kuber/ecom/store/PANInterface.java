package com.inspirado.kuber.ecom.store;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface PANInterface {

    @Multipart
    @POST("pan")
    Call<String> uploadImage(
            @Part MultipartBody.Part file, @Part("file") RequestBody name
    );

}