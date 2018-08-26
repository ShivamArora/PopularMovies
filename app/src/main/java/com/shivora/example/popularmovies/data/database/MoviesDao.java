package com.shivora.example.popularmovies.data.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.shivora.example.popularmovies.data.Movie;

import java.util.List;

@Dao
public interface MoviesDao {
    @Query("SELECT * FROM movies")
    LiveData<List<Movie>> getFavoriteMovies();

    @Insert
    void addToFavorites(Movie movie);

    @Delete
    void deleteFromFavorites(Movie movie);

    @Query("SELECT * FROM movies WHERE movieId=:movieId")
    LiveData<Movie> loadMovieById(int movieId);
}
