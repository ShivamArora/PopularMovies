package com.shivora.example.popularmovies.data.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

import com.shivora.example.popularmovies.data.Movie;

@Database(entities = {Movie.class},version = 1,exportSchema = false)
public abstract class MoviesDatabase extends RoomDatabase {
    private static final String TAG = MoviesDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "moviedb";

    private static MoviesDatabase sInstance;

    public static MoviesDatabase getsInstance(Context context){
        if (sInstance == null){
            synchronized (LOCK){
                Log.d(TAG, "getsInstance: Creating database instance");

                sInstance = Room.databaseBuilder(context.getApplicationContext(),MoviesDatabase.class,DATABASE_NAME)
                        .build();
            }
        }
        return sInstance;
    }

    public abstract MoviesDao moviesDao();
}
