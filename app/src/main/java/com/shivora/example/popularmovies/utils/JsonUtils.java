package com.shivora.example.popularmovies.utils;

import com.shivora.example.popularmovies.data.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {
    public static final String BASE_URL_POSTER = "http://image.tmdb.org/t/p/w500";

    public static List<Movie> parseDiscoverMovies(String json){
        Movie movie;
        List<Movie> moviesList = new ArrayList<>();
        try{
            JSONObject rootObject = new JSONObject(json);
            JSONArray results = rootObject.getJSONArray("results");

            for (int i=0;i<results.length();i++){
                JSONObject movieObject = results.getJSONObject(i);
                int id = movieObject.getInt("id");
                String title = movieObject.getString("title");
                float rating = Float.parseFloat(String.valueOf(movieObject.get("vote_average")));
                String posterUrl = BASE_URL_POSTER + movieObject.getString("poster_path");
                movie = new Movie(id,title,posterUrl,rating);
                moviesList.add(movie);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        return moviesList;
    }


}
