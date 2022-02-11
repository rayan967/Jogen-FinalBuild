package com.example.jogen.ViewModels;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jogen.Repositories.AnimeListItem;
import com.example.jogen.Repositories.AnimeListRoomDatabase;
import com.example.jogen.Repositories.HomeRepository;
import com.example.jogen.Repositories.SearchListItem;
import com.example.jogen.Repositories.SearchRepository;

import java.util.HashMap;
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

    public void runQuery(String query){
        mRepository=new SearchRepository(app, query);
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