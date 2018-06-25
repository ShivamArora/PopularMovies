package com.shivora.example.popularmovies.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MoviesList {
    @SerializedName("results")
    private List<Movie> moviesList;

    public List<Movie> getMoviesList() {
        return moviesList;
    }

    public void setMoviesList(List<Movie> moviesList) {
        this.moviesList = moviesList;
    }
}
