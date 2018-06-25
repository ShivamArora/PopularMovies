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
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.shivora.example.popularmovies.R;
import com.shivora.example.popularmovies.adapters.MoviesAdapter;
import com.shivora.example.popularmovies.data.Movie;
import com.shivora.example.popularmovies.data.MoviesList;
import com.shivora.example.popularmovies.utils.ConnectionUtils;
import com.shivora.example.popularmovies.utils.JsonUtils;
import com.shivora.example.popularmovies.utils.NetworkingUtils;
import com.shivora.example.popularmovies.utils.TheMovieDbApi;

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
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.view.View.GONE;
import static android.view.View.OVER_SCROLL_NEVER;

public class DiscoverMoviesActivity extends AppCompatActivity implements MoviesAdapter.MovieItemClickListener{

    public static final String EXTRA_MOVIE = "movie";
    @BindView(R.id.recycler_view_movies)
    RecyclerView recyclerView;
    @BindView(R.id.tv_empty_list)
    TextView tvEmptyList;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.bottom_sheet_sort_options) View sortOptionsBottomSheet;
    @BindView(R.id.tv_sorted_by) TextView tvSortedBy;
    @BindView(R.id.imageView)
    ImageView bottomSheetIcon;


    @BindString(R.string.movies_api_key) String apiKey;
    @BindString(R.string.span_count) String spanCount;

    private static final String POPULARITY = "popular";
    private static final String TOP_RATED = "top_rated";

    private BottomSheetBehavior bottomSheetBehavior;

    MoviesAdapter adapter;
    SortOrder sortOrder = SortOrder.SORT_BY_POPULARITY;
    String jsonResult;
    private static final String TAG = DiscoverMoviesActivity.class.getSimpleName();
    private static List<Movie> moviesList;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_movies);
        ButterKnife.bind(this);

        context = DiscoverMoviesActivity.this;
        moviesList = new ArrayList<>();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,Integer.parseInt(spanCount));
        recyclerView.setLayoutManager(gridLayoutManager);

        adapter = new MoviesAdapter(moviesList,this);
        recyclerView.setAdapter(adapter);

        setupSortOptionsBottomSheet();
        getMoviesList();
    }

    private void setupSortOptionsBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(sortOptionsBottomSheet);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                bottomSheetIcon.setImageResource(
                                newState==BottomSheetBehavior.STATE_SETTLING||newState==BottomSheetBehavior.STATE_EXPANDED
                                ?R.drawable.ic_round_arrow_drop_down_circle_24px
                                :R.drawable.ic_round_arrow_up_circle_24px);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    private void showProgressBar(boolean visible){
        if(visible){
            recyclerView.setVisibility(GONE);
            progressBar.setVisibility(View.VISIBLE);
            tvEmptyList.setVisibility(GONE);
        }
        else{
            recyclerView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(GONE);
            tvEmptyList.setVisibility(GONE);
        }
    }

    private void showErrorView(boolean visible,String errorMsg){
        if (visible){
            recyclerView.setVisibility(GONE);
            progressBar.setVisibility(GONE);
            tvEmptyList.setVisibility(View.VISIBLE);
            tvEmptyList.setText(errorMsg);
        }
        else{
            recyclerView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(GONE);
            tvEmptyList.setVisibility(GONE);
        }
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
    public void sortByPopular(){
        sortOrder = SortOrder.SORT_BY_POPULARITY;
        tvSortedBy.setText(R.string.popular);
        collapseSortOptions();
        getMoviesList();
    }

    @OnClick(R.id.layout_sort_by_top_rated)
    public void sortByTopRated(){
        sortOrder = SortOrder.SORT_BY_TOP_RATED;
        tvSortedBy.setText(R.string.top_rated);
        collapseSortOptions();
        getMoviesList();
    }

    @Override
    public void onMovieItemClick(int position) {
        Movie movie = moviesList.get(position);
        startMovieDetailsActivity(movie);
    }

    private void startMovieDetailsActivity(Movie movie){
        Intent moviesActivity = new Intent(DiscoverMoviesActivity.this,MovieDetailsActivity.class);
        moviesActivity.putExtra(EXTRA_MOVIE,movie);
        startActivity(moviesActivity);
    }

    //Sort Order enumerable
    enum SortOrder{
        SORT_BY_POPULARITY,
        SORT_BY_TOP_RATED
    }

    //Retrofit Implementation

    public void getMoviesList(){
        if (!ConnectionUtils.haveNetworkConnection(context)){
            showErrorView(true,getString(R.string.no_internet_connection));
            return;
        }
        else{
            showProgressBar(true);
        }

        //Retrofit
        Retrofit retrofit = NetworkingUtils.getRetrofitInstance(apiKey);

        TheMovieDbApi movieDbApi = retrofit.create(TheMovieDbApi.class);

        Call<MoviesList> call = movieDbApi.getMoviesList(sortOrder==SortOrder.SORT_BY_POPULARITY?POPULARITY:TOP_RATED);

        call.enqueue(new Callback<MoviesList>() {
            @Override
            public void onResponse(Call<MoviesList> call, Response<MoviesList> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: Success");
                    Log.d(TAG, "onResponse: ");

                    showProgressBar(false);
                    setMoviesList(response.body().getMoviesList());
                }
            }

            @Override
            public void onFailure(Call<MoviesList> call, Throwable t) {
               // Toast.makeText(context,"Failed to fetch data!",Toast.LENGTH_LONG).show();
                Log.d(TAG, "onFailure: "+t.getMessage());
                showErrorView(true,t.getMessage());
            }
        });
        Log.d(TAG, "getMoviesList: Fetching Movies");
    }

    private void setMoviesList(List<Movie> movies) {
        moviesList = movies;
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
