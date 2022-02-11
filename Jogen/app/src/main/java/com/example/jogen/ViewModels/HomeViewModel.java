package com.example.jogen.ViewModels;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
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

import java.util.HashMap;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private HomeRepository mRepository;
    private LiveData<List<AnimeListItem>> mAllRows;
    LiveData<List<AnimeListItem>> list;
    private MutableLiveData<String> mText;
    Application app;


    public HomeViewModel(Application application) {
        super(application);
        app=application;

    }

    public void populate(Activity activity){
        mRepository=new HomeRepository(app, activity);
        mAllRows=mRepository.getAllRows();
        list=new MutableLiveData<>();
    }

    public LiveData<List<AnimeListItem>> getAllRows() {
        return mAllRows;
    }

    public void insert(AnimeListItem tt) {
        mRepository.insert(tt);
    }

    public static String getUser(Application app){
        String name;
        SharedPreferences pref= app.getSharedPreferences("AppPref", MODE_PRIVATE);
        name=pref.getString("Username","");

        return name;
    }


    public LiveData<List<AnimeListItem>> getList(String status)
    {
        getListof(status);
        return list;
    }

    public LiveData<List<AnimeListItem>> getListof(String status){
        AnimeListRoomDatabase INSTANCE = AnimeListRoomDatabase.getDatabase(app);
        this.list=INSTANCE.alDao().getList(status);
        return list;
    }

    public void refresh(Activity activity){
        mRepository.refresh(activity);
    }

}