package com.extentia.imagetotext.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.extentia.imagetotext.ImageToTextApp;
import com.extentia.imagetotext.api.Repository;

public class BaseViewModel extends AndroidViewModel {
    Repository kaustRepository;
    int pageSize = 15;

    public BaseViewModel(@NonNull Application application) {
        super(application);
        if (kaustRepository == null)
            kaustRepository = ((ImageToTextApp) application).getAppRepository();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
