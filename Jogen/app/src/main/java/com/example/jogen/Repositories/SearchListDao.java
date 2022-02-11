package com.example.jogen.Repositories;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import java.util.List;


@Dao
public interface SearchListDao {

    @Insert
    void insert(SearchListItem tt);

    @Query("DELETE FROM search_list")
    void deleteAll();

    @Query("SELECT * from search_list")
    LiveData<List<SearchListItem>> getAllRows();

    @Query("SELECT * from search_list")
    List<SearchListItem> getAllRowsL();


}
