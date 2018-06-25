package com.shivora.example.popularmovies.utils;

import com.shivora.example.popularmovies.data.MoviesList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TheMovieDbApi {
    @GET("/3/movie/{sort_by}")
    Call<MoviesList> getMoviesList(@Path("sort_by") String sortBy);
}
