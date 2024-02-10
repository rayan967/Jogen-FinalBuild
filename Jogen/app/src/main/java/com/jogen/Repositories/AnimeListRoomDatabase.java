package com.jogen.Repositories;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


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