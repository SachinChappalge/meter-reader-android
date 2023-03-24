package com.extentia.imagetotext.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.extentia.imagetotext.api.ApiInterface;
import com.extentia.imagetotext.api.NetworkState;

import java.io.File;

public class ImageViewModel extends BaseViewModel{


    public ImageViewModel(@NonNull Application application) {
        super(application);
    }

    private LiveData<NetworkState> networkStateMutableLiveData;
    private ApiInterface apiInterface;

    public void uploadImage(ApiInterface apiInterface, String minAngle, String maxAngle, String minValue, String maxValue, String units, File imageFile, boolean isConnectedToNetwork) {
        networkStateMutableLiveData = kaustRepository.uploadPhoto(apiInterface, minAngle, maxAngle, minValue, maxValue, units, imageFile, isConnectedToNetwork);
    }

    public LiveData<NetworkState> getNetworkState() {
        return networkStateMutableLiveData;
    }
}
