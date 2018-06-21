package com.shivora.example.popularmovies.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shivora.example.popularmovies.R;
import com.shivora.example.popularmovies.adapters.MoviesAdapter;
import com.shivora.example.popularmovies.data.Movie;
import com.shivora.example.popularmovies.utils.ConnectionUtils;
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
import butterknife.OnClick;

import static android.view.View.GONE;

public class DiscoverMoviesActivity extends AppCompatActivity implements MoviesAdapter.MovieItemClickListener{

    @BindView(R.id.recycler_view_movies)
    RecyclerView recyclerView;
    @BindView(R.id.tv_empty_list)
    TextView tvEmptyList;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.bottom_sheet_sort_options) View sortOptionsBottomSheet;
    @BindView(R.id.tv_sorted_by) TextView tvSortedBy;


    @BindString(R.string.movies_api_key) String apiKey;
    @BindString(R.string.span_count) String spanCount;

    private static final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
    private static final String POPULARITY = "popularity.desc";
    private static final String TOP_RATED = "vote_average.desc";
    private static final String API_KEY = "api_key";

    private BottomSheetBehavior bottomSheetBehavior;

    MoviesAdapter adapter;
    SortOrder sortOrder = SortOrder.SORT_BY_POPULARITY;
    String jsonResult;
    private static final String TAG = DiscoverMoviesActivity.class.getSimpleName();
    List<Movie> moviesList;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_movies);
        ButterKnife.bind(this);

        context = DiscoverMoviesActivity.this;
        moviesList = new ArrayList<>();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,Integer.parseInt(spanCount));
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(gridLayoutManager);
        Log.d(TAG, "onCreate: ");
        adapter = new MoviesAdapter(moviesList,this);

        recyclerView.setAdapter(adapter);
        setupSortOptionsBottomSheet();
        new GetMoviesList().execute();

    }

    private void setupSortOptionsBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(sortOptionsBottomSheet);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    @OnClick(R.id.layout_sorted_by)
    public void toggleSortOptions(){
        if (bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_COLLAPSED)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        else
            collapseSortOptions();
    }

    public void collapseSortOptions(){
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @OnClick(R.id.layout_sort_by_popular)
    public  void sortByPopular(){
        sortOrder = SortOrder.SORT_BY_POPULARITY;
        tvSortedBy.setText(R.string.popular);
        collapseSortOptions();
        new GetMoviesList().execute();
    }

    @OnClick(R.id.layout_sort_by_top_rated)
    public void sortByTopRated(){
        sortOrder = SortOrder.SORT_BY_TOP_RATED;
        tvSortedBy.setText(R.string.top_rated);
        collapseSortOptions();
        new GetMoviesList().execute();
    }


    class GetMoviesList extends AsyncTask<Void,Void,String>{
        URL url;
        @Override
        protected void onPreExecute() {
            url = buildURL(sortOrder);
            Log.d(TAG, "onPreExecute: "+ConnectionUtils.haveNetworkConnection(context));
            if (!ConnectionUtils.haveNetworkConnection(context)) {
                progressBar.setVisibility(GONE);
                recyclerView.setVisibility(GONE);
                tvEmptyList.setText("Not connected to Internet!");
                tvEmptyList.setVisibility(View.VISIBLE);
                cancel(true);
            }
            else {
                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(GONE);
                tvEmptyList.setVisibility(GONE);
            }
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

    @Override
    public void onMovieItemClick(int position) {
        Movie movie = moviesList.get(position);
        int movieId = movie.getMovieId();
        startMovieDetailsActivity(movieId);
    }

    private void startMovieDetailsActivity(int movieId){
        Intent moviesActivity = new Intent(DiscoverMoviesActivity.this,MovieDetailsActivity.class);
        moviesActivity.putExtra("movie_id",movieId);
        startActivity(moviesActivity);
    }

    enum SortOrder{
        SORT_BY_POPULARITY,
        SORT_BY_TOP_RATED
    }
}
