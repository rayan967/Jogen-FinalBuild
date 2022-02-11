package com.example.jogen.ViewModelFactories;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.jogen.ViewModels.AnimeViewModel;

public class AnimeViewModelFactory implements ViewModelProvider.Factory {

    private Application mApplication;
    private String mExtra;
    private Activity mExtra2;


    public AnimeViewModelFactory(Application application, String extra, Activity extra2) {
        mApplication = application;
        mExtra = extra;
        mExtra2=extra2;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AnimeViewModel(mApplication, mExtra, mExtra2);
    }
}