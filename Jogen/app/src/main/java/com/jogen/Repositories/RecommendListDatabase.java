package com.jogen.Repositories;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {SearchListItem.class}, version = 1, exportSchema = false)
public abstract class RecommendListDatabase extends RoomDatabase {
    public abstract SearchListDao alDao();

    private static RecommendListDatabase INSTANCE;

    public static RecommendListDatabase getDatabase(final Context context) {
        if (INSTANCE == null){
            synchronized (RecommendListDatabase.class) {

                if (INSTANCE == null) {


                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RecommendListDatabase.class, "reclist_database")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();

                }
            }
    }
        return INSTANCE;

    }



}