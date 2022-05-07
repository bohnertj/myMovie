package com.mobileapplication.mymovie_10.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {MovieEntity.class, AppSettingsEnity.class}, version = 1, exportSchema = false)
public abstract class MyMovieDatabase extends RoomDatabase {

    private static final String DB_NAME = "myMovie.db";

    private static MyMovieDatabase SINGLETON_INSTANCE = null;

    public static MyMovieDatabase getSingletonInstance(Context context) {

        if (SINGLETON_INSTANCE == null) {
            SINGLETON_INSTANCE =
                    Room.databaseBuilder(
                            context.getApplicationContext(),
                            MyMovieDatabase.class,
                            DB_NAME).allowMainThreadQueries().build();
        }
        return SINGLETON_INSTANCE;
    }
    public abstract MovieDAO movieDao();
}
