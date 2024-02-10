package com.jogen.ViewModels;


import android.app.Activity;
import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.jogen.Repositories.RecommendADatabase;
import com.jogen.Repositories.RecommendListAItem;
import com.jogen.Repositories.RecommendRepository;

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