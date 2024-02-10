package com.jogen.ViewModelFactories;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.jogen.ViewModels.DiscoverViewModel;

public class DiscoverViewModelFactory implements ViewModelProvider.Factory {

    private Application mApplication;
    private String mExtra;


    public DiscoverViewModelFactory(Application application, String extra) {
        mApplication = application;
        mExtra = extra;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new DiscoverViewModel(mApplication);
    }
}