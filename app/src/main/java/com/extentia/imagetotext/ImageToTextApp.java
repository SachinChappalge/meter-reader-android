package com.extentia.imagetotext;


import android.app.Application;

import com.extentia.imagetotext.api.ApiClient;
import com.extentia.imagetotext.api.ApiInterface;
import com.extentia.imagetotext.api.Repository;


public class ImageToTextApp extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
    }

    public ApiInterface getApiInterface() {
        return ApiClient.getClient().create(ApiInterface.class);
    }

    public Repository getAppRepository() {
        return Repository.getInstance(getApiInterface());
    }
}
