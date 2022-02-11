package com.example.jogen.Repositories;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import java.util.List;


@Dao
public interface RecommendListADao {

    @Insert
    void insert(RecommendListAItem tt);

    @Query("DELETE FROM recommend_list")
    void deleteAll();

    @Query("SELECT * from recommend_list")
    LiveData<List<RecommendListAItem>> getAllRows();

    @Query("SELECT * from recommend_list where type='art'")
    List<RecommendListAItem> getArtRows();

    @Query("SELECT * from recommend_list where type='sound'")
    List<RecommendListAItem> getSoundRows();

    @Query("SELECT * from recommend_list where type='vibe'")
    List<RecommendListAItem> getVibeRows();

    @Query("SELECT * from recommend_list where type='humor'")
    List<RecommendListAItem> getHumorRows();

    @Query("SELECT * from recommend_list")
    List<RecommendListAItem> getAllRowsL();



}
