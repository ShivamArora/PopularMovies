package com.shivora.example.popularmovies.utils;

import com.shivora.example.popularmovies.activities.DiscoverMoviesActivity;
import com.shivora.example.popularmovies.data.Movie;
import com.shivora.example.popularmovies.data.MoviesList;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TheMovieDbApi {
    @GET("/3/movie/{sort_by}")
    Call<MoviesList> getMoviesList(@Path("sort_by") String sortBy);
}
