package com.jogen.Repositories;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

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