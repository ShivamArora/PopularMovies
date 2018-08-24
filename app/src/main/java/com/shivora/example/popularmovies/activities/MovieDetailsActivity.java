package com.shivora.example.popularmovies.activities;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.shivora.example.popularmovies.R;
import com.shivora.example.popularmovies.adapters.MovieReviewsAdapter;
import com.shivora.example.popularmovies.adapters.MovieTrailersAdapter;
import com.shivora.example.popularmovies.data.Movie;
import com.shivora.example.popularmovies.data.MovieReviewsList;
import com.shivora.example.popularmovies.data.MovieTrailersList;
import com.shivora.example.popularmovies.data.MovieTrailersList.MovieTrailer;
import com.shivora.example.popularmovies.utils.ConnectionUtils;
import com.shivora.example.popularmovies.utils.NetworkingUtils;
import com.shivora.example.popularmovies.utils.TheMovieDbApi;

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
import static com.shivora.example.popularmovies.utils.NetworkingUtils.BASE_URL_POSTER;
import static com.shivora.example.popularmovies.utils.NetworkingUtils.QUALITY_500;

public class MovieDetailsActivity extends AppCompatActivity implements MovieTrailersAdapter.MovieTrailerClickListener{

    private static final String TAG = MovieDetailsActivity.class.getSimpleName();

    @BindView(R.id.tv_movie_name)
    TextView tvMovieName;
    @BindView(R.id.iv_poster_image)
    ImageView ivPosterImage;
    @BindView(R.id.tv_release_date)
    TextView tvReleaseDate;
    @BindView(R.id.tv_rating)
    TextView tvRating;
    @BindView(R.id.tv_movie_plot)
    TextView tvMoviePlot;
    @BindView(android.R.id.content)
            View rootView;
    @BindView(R.id.recycler_view_movie_trailers)
    RecyclerView recyclerView;
    @BindView(R.id.recycler_view_movie_reviews)
    RecyclerView reviewsRecyclerView;
    @BindView(R.id.imageView)
    ImageView bottomSheetIcon;
    @BindView(R.id.bottom_sheet_reviews)
    View bottomSheetReviews;
    @BindView(R.id.tv_no_reviews_found)
    TextView tvNoReviewsFound;

    @BindString(R.string.movies_api_key)
            String apiKey;


    Movie movie;
    private Context context;
    private MovieTrailersAdapter mAdapter;
    private MovieReviewsAdapter mReviewsAdapter;

    private BottomSheetBehavior mReviewsBottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        //Enable back navigation
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        context = MovieDetailsActivity.this;
        movie = getIntent().getParcelableExtra(DiscoverMoviesActivity.EXTRA_MOVIE);

        //RecyclerView Implementation

        setMovieDetails(movie);

        getMovieTrailersList();

        getMovieReviewsList();

        setupReviewsBottomSheet();
    }

    private void setupReviewsBottomSheet() {
        mReviewsBottomSheetBehavior = BottomSheetBehavior.from(bottomSheetReviews);
        mReviewsBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
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

    @OnClick(R.id.layout_reviews_label)
    public void toggleReviewsVisibility(){
        if (mReviewsBottomSheetBehavior.getState()==BottomSheetBehavior.STATE_COLLAPSED)
            mReviewsBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        else
            mReviewsBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }
    //Retrofit implementation
    private void getMovieReviewsList() {
        if (!ConnectionUtils.haveNetworkConnection(context)){
            Snackbar.make(rootView,R.string.no_internet_connection,Snackbar.LENGTH_LONG).show();
            return;
        }

        Retrofit retrofit = NetworkingUtils.getRetrofitInstance(apiKey);
        TheMovieDbApi movieDbApi = retrofit.create(TheMovieDbApi.class);
        Call<MovieReviewsList> call = movieDbApi.getMovieReviewsList(String.valueOf(movie.getMovieId()));
        call.enqueue(new Callback<MovieReviewsList>() {
            @Override
            public void onResponse(Call<MovieReviewsList> call, Response<MovieReviewsList> response) {
                Log.d(TAG,"Reviews List: "+ response.body().getMovieReviewsList());
                List<MovieReviewsList.MovieReview> movieReviewList = response.body().getMovieReviewsList();
                if (movieReviewList.size()>0){
                    tvNoReviewsFound.setVisibility(GONE);
                    reviewsRecyclerView.setVisibility(View.VISIBLE);
                    setMovieReviews(response.body().getMovieReviewsList());
                }
                else{
                    tvNoReviewsFound.setVisibility(View.VISIBLE);
                    reviewsRecyclerView.setVisibility(GONE);
                }
            }

            @Override
            public void onFailure(Call<MovieReviewsList> call, Throwable t) {

            }
        });
    }

    public void getMovieTrailersList(){

        if (!ConnectionUtils.haveNetworkConnection(context)){
            Snackbar.make(rootView,R.string.no_internet_connection,Snackbar.LENGTH_LONG).show();
            return;
        }
        else{
            //Continue
        }

        //Retrofit
        Retrofit retrofit = NetworkingUtils.getRetrofitInstance(apiKey);

        TheMovieDbApi movieDbApi = retrofit.create(TheMovieDbApi.class);

        Call<MovieTrailersList> call = movieDbApi.getMovieTrailersList(String.valueOf(movie.getMovieId()));

        call.enqueue(new Callback<MovieTrailersList>() {
            @Override
            public void onResponse(Call<MovieTrailersList> call, Response<MovieTrailersList> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: Success");
                    Log.d(TAG, "Trailers List:  "+response.body());

                    //showProgressBar(false);
                    //set movie trailers list
                    setMovieTrailers(response.body().getMovieTrailerList());
                }
            }

            @Override
            public void onFailure(Call<MovieTrailersList> call, Throwable t) {
                // Toast.makeText(context,"Failed to fetch data!",Toast.LENGTH_LONG).show();
                Log.d(TAG, "onFailure: "+t.getMessage());
                //show error msg
            }
        });
        Log.d(TAG, "getMoviesList: Fetching Movies");
    }

    private void setMovieDetails(Movie movie) {
        tvMovieName.setText(movie.getMovieTitle());
        tvReleaseDate.setText(movie.getMovieReleaseDate());
        tvRating.setText(String.valueOf(movie.getMovieRating()));
        tvMoviePlot.setText(movie.getMoviePlot());

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.placeholder);
        requestOptions.error(R.drawable.error_placeholder);

        Glide.with(MovieDetailsActivity.this).load(BASE_URL_POSTER+QUALITY_500+movie.getMoviePosterUrl()).apply(requestOptions).transition(DrawableTransitionOptions.withCrossFade()).into(ivPosterImage);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //Retrofit Implementation

    private void setMovieTrailers(List<MovieTrailer> movieTrailerList) {
        mAdapter = new MovieTrailersAdapter(movieTrailerList,this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

    }

    private void setMovieReviews(List<MovieReviewsList.MovieReview> movieReviewsList){
        mReviewsAdapter = new MovieReviewsAdapter(movieReviewsList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        reviewsRecyclerView.setLayoutManager(layoutManager);
        reviewsRecyclerView.setAdapter(mReviewsAdapter);
    }

    @Override
    public void onMovieTrailerClick(String key) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.youtube.com/watch?v="+key));
        if (intent.resolveActivity(getPackageManager())!=null){
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if (mReviewsBottomSheetBehavior.getState()==mReviewsBottomSheetBehavior.STATE_EXPANDED){
            mReviewsBottomSheetBehavior.setState(mReviewsBottomSheetBehavior.STATE_COLLAPSED);
        }
        else{
            finish();
        }
    }
}
