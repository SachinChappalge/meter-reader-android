package com.extentia.imagetotext.api;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.extentia.imagetotext.model.ImageScanRequest;
import com.extentia.imagetotext.model.ScanResponse;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {

    private ApiInterface apiInterface;
    private static Repository sInstance;
    private MutableLiveData networkState;
    public Repository(ApiInterface apiInterface)
    {
        this.apiInterface=apiInterface;
    }

    public static Repository getInstance(ApiInterface serverApi) {
        if (sInstance == null) {
            synchronized (Repository.class) {
                if (sInstance == null) {
                    sInstance = new Repository(serverApi);
                }
            }
        }
        return sInstance;
    }


    public MutableLiveData<NetworkState> uploadPhoto(ApiInterface apiInterface, String minAngle, String maxAngle, String minValue, String maxValue, String units, File file, boolean isConnected) {
        networkState = new MediatorLiveData();
        try {
            if (isConnected) {
                MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
                RequestBody maxAngleBody = RequestBody.create(okhttp3.MultipartBody.FORM, maxAngle);
                RequestBody minAngleBody = RequestBody.create(okhttp3.MultipartBody.FORM, minAngle);
                RequestBody maxValueBody = RequestBody.create(okhttp3.MultipartBody.FORM, maxValue);
                RequestBody minValueBody = RequestBody.create(okhttp3.MultipartBody.FORM, minValue);
                RequestBody unitsBody = RequestBody.create(okhttp3.MultipartBody.FORM, units);
                apiInterface.uploadImageToScan(body, minAngleBody, maxAngleBody, minValueBody, maxValueBody, unitsBody).enqueue(new Callback<ScanResponse>() {
                    @Override
                    public void onResponse(Call<ScanResponse> call, Response<ScanResponse> response) {
                        if (response.isSuccessful()) {
                            networkState.postValue(new NetworkState(NetworkState.Status.SUCCESS, response.body().getReading()));
                        } else {
                            networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
                        }
                    }

                    @Override
                    public void onFailure(Call<ScanResponse> call, Throwable t) {
                        String errorMessage = t.getMessage();
                        networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));
                    }
                });
            } else {

            }
        } catch (Exception e) {
            networkState.postValue(new NetworkState(NetworkState.Status.FAILED, e.getMessage()));
        }
        return networkState;
    }
}
