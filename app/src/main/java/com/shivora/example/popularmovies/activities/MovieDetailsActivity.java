package com.shivora.example.popularmovies.activities;

import android.app.ActionBar;
import android.content.Context;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.shivora.example.popularmovies.R;
import com.shivora.example.popularmovies.data.Movie;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.shivora.example.popularmovies.utils.NetworkingUtils.BASE_URL_POSTER;
import static com.shivora.example.popularmovies.utils.NetworkingUtils.QUALITY_500;

public class MovieDetailsActivity extends AppCompatActivity {

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

    Movie movie;
    private Context context;

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

        setMovieDetails(movie);
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
}
