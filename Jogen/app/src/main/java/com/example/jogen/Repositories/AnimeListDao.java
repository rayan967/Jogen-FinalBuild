package com.example.jogen.Repositories;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import java.util.List;


@Dao
public interface AnimeListDao {

    @Insert
    void insert(AnimeListItem tt);

    @Query("DELETE FROM anime_list")
    void deleteAll();

    @Query("SELECT * from anime_list")
    LiveData<List<AnimeListItem>> getAllRows();

    @Query("SELECT * from anime_list")
    List<AnimeListItem> getAllRowsL();

    @Query("UPDATE anime_list SET status = :status where anime_id = :anime_id ")
    void setStatus(String status, String anime_id);

    @Query("UPDATE anime_list SET rating = :score where anime_id = :anime_id ")
    void setScore(String score, String anime_id);

    @Query("UPDATE anime_list SET eps_watched = :eps where anime_id = :anime_id ")
    void setEps(String eps, String anime_id);


    @Query("select * from anime_list where status = :status ")
    LiveData<List<AnimeListItem>> getList(String status);

    @Query("select * from anime_list where status = 'Completed' or status = 'Watching' or status = 'On Hold'")
    List<AnimeListItem> getList();
    
}
