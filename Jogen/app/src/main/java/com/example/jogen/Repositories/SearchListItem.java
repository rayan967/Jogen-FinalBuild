package com.example.jogen.Repositories;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "search_list")
public class SearchListItem {


    @ColumnInfo(name = "anime_name")
    public String anime_name;

    @ColumnInfo(name = "score")
    public String score;
    @ColumnInfo(name = "episodes")
    public String episodes;

    @PrimaryKey
    @ColumnInfo(name = "anime_id")
    @NonNull
    public String anime_id;

    @ColumnInfo(name = "poster", typeAffinity = ColumnInfo.BLOB)
    public byte[] poster;



    public SearchListItem(String anime_name, String episodes, String score, String anime_id, byte[] poster) {
        this.anime_name=anime_name;
        this.episodes=episodes;
        this.score=score;
        this.anime_id=anime_id;
        this.poster=poster;
    }

    public SearchListItem(){}


    public String getAnimeName() {
        return anime_name;
    }
    public void setAnimeName(String train_name) {
        this.anime_name = train_name;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getEpisodes() {
        return episodes;
    }

    public void setEpisodes(String episodes) {
        this.episodes = episodes;
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
