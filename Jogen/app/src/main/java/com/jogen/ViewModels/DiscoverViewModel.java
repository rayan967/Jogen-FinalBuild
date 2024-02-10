package com.jogen.ViewModels;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.jogen.Repositories.SearchListItem;
import com.jogen.Repositories.SearchRepository;

import java.util.List;

public class DiscoverViewModel extends AndroidViewModel {

    private SearchRepository mRepository;
    private LiveData<List<SearchListItem>> mAllRows;
    Application app;


    public DiscoverViewModel(Application application) {
        super(application);
        app=application;
        Log.d("Reached: ","3");
    }

    public void runQuery(String query, Activity activity){
        mRepository=new SearchRepository(app, query, activity);
        mAllRows=mRepository.getAllRows();
    }

    public void runRQuery(Activity activity){
        mRepository=new SearchRepository(app, activity);
        mAllRows=mRepository.getAllRows();

    }

    public void refresh(Activity activity)
    {
        mRepository.refresh(activity);
    }

    public LiveData<List<SearchListItem>> getAllRows() {
        return mAllRows;
    }

}