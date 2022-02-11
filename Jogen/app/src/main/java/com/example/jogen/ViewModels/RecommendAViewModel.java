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
import com.example.jogen.Repositories.RecommendADatabase;
import com.example.jogen.Repositories.RecommendListAItem;
import com.example.jogen.Repositories.RecommendRepository;
import com.example.jogen.Repositories.SearchListItem;
import com.example.jogen.Repositories.SearchRepository;

import java.util.HashMap;
import java.util.List;

public class RecommendAViewModel extends AndroidViewModel {

    private RecommendRepository mRepository;
    private LiveData<List<RecommendListAItem>> mAllRows;
    Application app;


    public RecommendAViewModel(Application application) {
        super(application);
        app=application;
        Log.d("Reached: ","2");
    }

    public void runQuery(Activity activity){
        mRepository=new RecommendRepository(app, activity);
        mAllRows=mRepository.getAllRows();
    }



    public LiveData<List<RecommendListAItem>> getAllRows() {
        return mAllRows;
    }


    public List<RecommendListAItem> getArtRows() {
        RecommendADatabase INSTANCE = RecommendADatabase.getDatabase(app);
        return INSTANCE.alDao().getArtRows();
    }

    public List<RecommendListAItem> getSoundRows() {
        RecommendADatabase INSTANCE = RecommendADatabase.getDatabase(app);
        return INSTANCE.alDao().getSoundRows();    }

    public List<RecommendListAItem> getVibeRows() {
        RecommendADatabase INSTANCE = RecommendADatabase.getDatabase(app);
        return INSTANCE.alDao().getVibeRows();
    }

    public List<RecommendListAItem> getHumorRows() {
        RecommendADatabase INSTANCE = RecommendADatabase.getDatabase(app);
        return INSTANCE.alDao().getHumorRows();
    }


    public void refresh(Activity activity){
        mRepository.refresh(activity);
    }


}