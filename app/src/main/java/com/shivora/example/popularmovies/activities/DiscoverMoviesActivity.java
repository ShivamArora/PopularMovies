package com.shivora.example.popularmovies.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.shivora.example.popularmovies.R;
import com.shivora.example.popularmovies.adapters.MoviesAdapter;
import com.shivora.example.popularmovies.data.Movie;
import com.shivora.example.popularmovies.data.database.MoviesDatabase;
import com.shivora.example.popularmovies.data.MoviesList;
import com.shivora.example.popularmovies.utils.ConnectionUtils;
import com.shivora.example.popularmovies.utils.NetworkingUtils;
import com.shivora.example.popularmovies.utils.TheMovieDbApi;
import com.shivora.example.popularmovies.utils.viewmodels.MoviesViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.view.View.GONE;

public class DiscoverMoviesActivity extends AppCompatActivity implements MoviesAdapter.MovieItemClickListener{

    public static final String EXTRA_MOVIE = "movie";
    public static final String KEY_SORT_ORDER = "sort_order";
    public static final String KEY_MOVIES_LIST = "movies_list";
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

        if (savedInstanceState != null){
            Log.i(TAG, "onCreate: sortOrder"+savedInstanceState.getInt(KEY_SORT_ORDER));
            restoreSortOrder(savedInstanceState);
            moviesList = savedInstanceState.getParcelableArrayList(KEY_MOVIES_LIST);
            setMoviesList(moviesList);
        }
        else{
            getMoviesList();
        }
        setupSortOptionsBottomSheet();
    }

    private void restoreSortOrder(Bundle savedInstanceState) {
        //Restore sortOrder
        switch (savedInstanceState.getInt(KEY_SORT_ORDER)){
            case 0:
                sortOrder = SortOrder.SORT_BY_POPULARITY;
                tvSortedBy.setText(R.string.popular);
                break;
            case 1:
                sortOrder = SortOrder.SORT_BY_TOP_RATED;
                tvSortedBy.setText(R.string.top_rated);
                break;
            case 2:
                sortOrder = SortOrder.SORT_BY_FAVORITES;
                tvSortedBy.setText(R.string.favorite_movies);
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SORT_ORDER, sortOrder.ordinal());
        outState.putParcelableArrayList(KEY_MOVIES_LIST, (ArrayList<Movie>) moviesList);
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

    @OnClick(R.id.layout_show_favorite_movies)
    public void showFavoriteMovies(){
        sortOrder = SortOrder.SORT_BY_FAVORITES;
        MoviesViewModel viewModel = ViewModelProviders.of(DiscoverMoviesActivity.this).get(MoviesViewModel.class);
        LiveData<List<Movie>> favoriteMoviesList = viewModel.getMoviesList();
        favoriteMoviesList.observe(DiscoverMoviesActivity.this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                if (sortOrder==SortOrder.SORT_BY_FAVORITES){
                    setMoviesList(movies);
                }
            }
        });
        tvSortedBy.setText(R.string.favorite_movies);
        collapseSortOptions();
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
    public enum SortOrder{
        SORT_BY_POPULARITY,
        SORT_BY_TOP_RATED,
        SORT_BY_FAVORITES
    }

    //Retrofit Implementation

    public List<Movie> getMoviesList(){
        if (!ConnectionUtils.haveNetworkConnection(context)){
            showErrorView(true,getString(R.string.no_internet_connection));
            return null;
        }
        else{
            showProgressBar(true);
        }

        //Retrofit
        Retrofit retrofit = NetworkingUtils.getRetrofitInstance(apiKey);

        TheMovieDbApi movieDbApi = retrofit.create(TheMovieDbApi.class);

        Log.i(TAG, "getMoviesList: sortOrder: "+sortOrder);
        Call<MoviesList> call = movieDbApi.getMoviesList(sortOrder==SortOrder.SORT_BY_POPULARITY?POPULARITY:TOP_RATED);

        call.enqueue(new Callback<MoviesList>() {
            @Override
            public void onResponse(Call<MoviesList> call, Response<MoviesList> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: Success");
                    Log.d(TAG, "onResponse: ");

                    showProgressBar(false);
                    moviesList = (ArrayList<Movie>) response.body().getMoviesList();
                    setMoviesList(moviesList);
                    recyclerView.scrollToPosition(0);
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
        for(Movie movie: moviesList){
            Log.i(TAG,movie.getMovieTitle());
        }
        return moviesList;
    }

    private void setMoviesList(List<Movie> movies) {
        moviesList = movies;
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
