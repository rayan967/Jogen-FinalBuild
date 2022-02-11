package com.example.jogen.Repositories;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "anime_list")
public class AnimeListItem {


    @ColumnInfo(name = "anime_name")
    public String anime_name;
    @ColumnInfo(name = "status")
    public String status;
    @ColumnInfo(name = "rating")
    public String rating;
    @ColumnInfo(name = "eps_watched")
    public String eps_watched;

    @PrimaryKey
    @ColumnInfo(name = "anime_id")
    @NonNull
    public String anime_id;

    @ColumnInfo(name = "poster", typeAffinity = ColumnInfo.BLOB)
    public byte[] poster;



    public AnimeListItem(String anime_name, String status, String rating, String eps_watched, String anime_id, byte[] poster) {
        this.anime_name=anime_name;
        this.status=status;
        this.rating=rating;
        this.eps_watched=eps_watched;
        this.anime_id=anime_id;
        this.poster=poster;
    }

    public AnimeListItem(){}


    public String getAnimeName() {
        return anime_name;
    }
    public void setAnimeName(String train_name) {
        this.anime_name = train_name;
    }


    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }


    public String getRating() {
        return rating;
    }
    public void setRating(String rating) {
        this.rating = rating;
    }


    public String getEpsWatched() {
        return eps_watched;
    }
    public void setEpsWatched(String eps_watched) {
        this.eps_watched = eps_watched;
    }

    public String getAnime_id() {
        return anime_id;
    }
    public void setAnime_id(String anime_id) {
        this.anime_id = anime_id;
    }

    public byte[] getPoster(){return poster;}
    public void setPoster(byte[] poster){this.poster=poster;}



}
