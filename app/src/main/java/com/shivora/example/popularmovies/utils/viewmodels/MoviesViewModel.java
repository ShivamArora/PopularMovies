package com.shivora.example.popularmovies.utils.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.shivora.example.popularmovies.data.Movie;
import com.shivora.example.popularmovies.data.database.MoviesDatabase;

import java.util.List;

public class MoviesViewModel extends AndroidViewModel {
    private LiveData<List<Movie>> moviesList;
    public MoviesViewModel(@NonNull Application application) {
        super(application);

        //Initialize the member variable used to cache data
        MoviesDatabase moviesDatabase = MoviesDatabase.getInstance(this.getApplication());
        moviesList = moviesDatabase.moviesDao().getFavoriteMovies();
    }

    public LiveData<List<Movie>> getMoviesList() {
            return moviesList;
    }
}
