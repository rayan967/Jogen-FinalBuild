package com.example.jogen.Repositories;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.jogen.ViewModels.HomeViewModel;
import com.kttdevelopment.mal4j.MyAnimeList;
import com.kttdevelopment.mal4j.anime.Anime;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Database(entities = {RecommendListAItem.class}, version = 1, exportSchema = false)
public abstract class RecommendADatabase extends RoomDatabase {
    public abstract RecommendListADao alDao();

    private static RecommendADatabase INSTANCE;

    public static RecommendADatabase getDatabase(final Context context) {
        if(INSTANCE==null) {
            synchronized (RecommendADatabase.class) {

                if (INSTANCE == null) {

                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RecommendADatabase.class, "recA_database")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
            return INSTANCE;
    }



}