package com.shivora.example.popularmovies.utils;

import com.shivora.example.popularmovies.data.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {
    public static final String BASE_URL_POSTER = "http://image.tmdb.org/t/p/";

    public static final String QUALITY_500 = "w500";

    public static final String QUALITY_ORIGINAL = "original";
    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_OVERVIEW = "overview";
    public static final String KEY_POSTER_PATH = "poster_path";
    public static final String KEY_VOTE_AVERAGE = "vote_average";
    public static final String KEY_RELEASE_DATE = "release_date";
    public static final String KEY_RESULTS = "results";
    private static final String KEY_BACKDROP_PATH = "backdrop_path";

    public static List<Movie> parseDiscoverMovies(String json){
        Movie movie;
        List<Movie> moviesList = new ArrayList<>();
        try{
            JSONObject rootObject = new JSONObject(json);
            JSONArray results = rootObject.getJSONArray(KEY_RESULTS);

            for (int i=0;i<results.length();i++){
                JSONObject movieObject = results.getJSONObject(i);
                int id = movieObject.getInt(KEY_ID);
                String title = movieObject.getString(KEY_TITLE);
                float rating = Float.parseFloat(String.valueOf(movieObject.get(KEY_VOTE_AVERAGE)));
                String posterUrl = BASE_URL_POSTER + QUALITY_500 + movieObject.getString(KEY_POSTER_PATH);
                movie = new Movie(id,title,posterUrl,rating);
                moviesList.add(movie);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        return moviesList;
    }

    public static Movie parseMovieDetails(String json){
        Movie movie = null;
        try {
            JSONObject movieObject = new JSONObject(json);
            int id = movieObject.getInt(KEY_ID);
            String title = movieObject.getString(KEY_TITLE);
            String plot = movieObject.getString(KEY_OVERVIEW);
            String posterUrl = BASE_URL_POSTER + QUALITY_500 + movieObject.getString(KEY_POSTER_PATH);
            float rating = Float.parseFloat(String.valueOf(movieObject.get(KEY_VOTE_AVERAGE)));
            String releaseDate = movieObject.getString(KEY_RELEASE_DATE);

            movie = new Movie(id,title,posterUrl,rating,plot,releaseDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movie;
    }
}
