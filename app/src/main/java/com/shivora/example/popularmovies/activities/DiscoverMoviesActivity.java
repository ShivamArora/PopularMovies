package com.shivora.example.popularmovies.activities;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shivora.example.popularmovies.R;
import com.shivora.example.popularmovies.adapters.MoviesAdapter;
import com.shivora.example.popularmovies.data.Movie;
import com.shivora.example.popularmovies.utils.JsonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;

public class DiscoverMoviesActivity extends AppCompatActivity {

    @BindView(R.id.recycler_view_movies)
    RecyclerView recyclerView;
    @BindView(R.id.tv_empty_list)
    TextView tvEmptyList;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindString(R.string.movies_api_key) String apiKey;
    @BindString(R.string.span_count) String spanCount;

    private static final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
    private static final String POPULARITY = "popularity.desc";
    private static final String TOP_RATED = "vote_average.desc";
    private static final String API_KEY = "api_key";

    MoviesAdapter adapter;
    SortOrder sortOrder = SortOrder.SORT_BY_POPULARITY;
    String jsonResult;
    private static final String TAG = DiscoverMoviesActivity.class.getSimpleName();
    List<Movie> moviesList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_movies);
        ButterKnife.bind(this);

        moviesList = new ArrayList<>();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,Integer.parseInt(spanCount));
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(gridLayoutManager);

        adapter = new MoviesAdapter(moviesList);

        recyclerView.setAdapter(adapter);

        new GetMoviesList().execute();


    }

    class GetMoviesList extends AsyncTask<Void,Void,String>{
        URL url;
        @Override
        protected void onPreExecute() {
            url = buildURL(sortOrder);

            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(GONE);
            tvEmptyList.setVisibility(GONE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            String json = null;
            try {
                Log.i(TAG,"URL: "+url.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                Log.i(TAG,"Response Code: "+urlConnection.getResponseCode());
                if (urlConnection.getResponseCode()==HttpURLConnection.HTTP_OK){
                    InputStream inputStream = urlConnection.getInputStream();
                    Scanner scanner = new Scanner(inputStream);
                    scanner.useDelimiter("\\A");

                    if (scanner.hasNext())
                        json = scanner.next();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            jsonResult = result;
            Log.i(TAG,jsonResult);

            moviesList = JsonUtils.parseDiscoverMovies(jsonResult);
            for(Movie movie: moviesList){
                Log.i(TAG,movie.getMovieTitle());
            }

            if (moviesList==null){
                progressBar.setVisibility(GONE);
                recyclerView.setVisibility(GONE);
                tvEmptyList.setVisibility(View.VISIBLE);
            }
            else{
                progressBar.setVisibility(GONE);
                recyclerView.setVisibility(View.VISIBLE);
                tvEmptyList.setVisibility(GONE);
            }
            adapter.setMoviesList(moviesList);
        }
    }

    private URL buildURL(SortOrder sortOrder){
        URL url = null;
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter("sort_by",sortOrder==SortOrder.SORT_BY_POPULARITY?POPULARITY:TOP_RATED)
                .appendQueryParameter(API_KEY,apiKey)
                .build();

        try{
            url = new URL(uri.toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }

    enum SortOrder{
        SORT_BY_POPULARITY,
        SORT_BY_TOP_RATED
    }
}
