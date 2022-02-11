package com.example.jogen.Repositories;


import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;


import com.google.gson.Gson;
import com.kttdevelopment.mal4j.Authorization;
import com.kttdevelopment.mal4j.MyAnimeList;
import com.kttdevelopment.mal4j.MyAnimeListAuthenticator;
import com.kttdevelopment.mal4j.PaginatedIterator;
import com.kttdevelopment.mal4j.anime.Anime;
import com.kttdevelopment.mal4j.anime.AnimeListStatus;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;



@Database(entities = {AnimeListItem.class}, version = 1, exportSchema = false)
public abstract class AnimeListRoomDatabase extends RoomDatabase {
    public abstract AnimeListDao alDao();

    private static AnimeListRoomDatabase INSTANCE;

    public static AnimeListRoomDatabase getDatabase(final Context context) {
        if(INSTANCE==null)
        synchronized (AnimeListRoomDatabase.class) {

            if (INSTANCE == null) {

                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        AnimeListRoomDatabase.class, "animelist_database")
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        return INSTANCE;
    }



}