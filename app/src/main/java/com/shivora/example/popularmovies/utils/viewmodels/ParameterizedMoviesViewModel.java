package com.shivora.example.popularmovies.utils.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.shivora.example.popularmovies.data.Movie;
import com.shivora.example.popularmovies.data.database.MoviesDatabase;

public class ParameterizedMoviesViewModel extends ViewModel {
    private LiveData<Movie> movieLiveData;

    public ParameterizedMoviesViewModel(MoviesDatabase moviesDatabase,int movieId) {
        movieLiveData = moviesDatabase.moviesDao().loadMovieById(movieId);
    }

    public LiveData<Movie> getMovieLiveData() {
        return movieLiveData;
    }
}
