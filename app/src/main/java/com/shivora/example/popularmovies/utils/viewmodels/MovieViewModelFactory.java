package com.shivora.example.popularmovies.utils.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.shivora.example.popularmovies.data.database.MoviesDatabase;

public class MovieViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private MoviesDatabase moviesDatabase;
    private final int movieId;

    public MovieViewModelFactory(MoviesDatabase moviesDatabase,int movieId){
        this.moviesDatabase = moviesDatabase;
        this.movieId = movieId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ParameterizedMoviesViewModel(moviesDatabase,movieId);
    }
}
