package com.extentia.imagetotext.api;


import androidx.annotation.Keep;

import com.extentia.imagetotext.model.ScanResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

@Keep
public interface ApiInterface {
    @Multipart
    @POST("/scan/")
    Call<ScanResponse> uploadImageToScan(@Part MultipartBody.Part imageFile,
                                         @Part("min_angle") RequestBody minAngle,
                                         @Part("max_angle") RequestBody maxAngle,
                                         @Part("min_value") RequestBody minValue,
                                         @Part("max_value") RequestBody maxValue,
                                         @Part("units") RequestBody units);

}
