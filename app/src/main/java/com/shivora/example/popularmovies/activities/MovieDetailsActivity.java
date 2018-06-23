package com.shivora.example.popularmovies.activities;

import android.app.ActionBar;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.shivora.example.popularmovies.R;
import com.shivora.example.popularmovies.data.Movie;
import com.shivora.example.popularmovies.utils.ConnectionUtils;
import com.shivora.example.popularmovies.utils.JsonUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://api.themoviedb.org/3/movie";
    private static final String API_KEY = "api_key";
    private static final String TAG = MovieDetailsActivity.class.getSimpleName();

    @BindString(R.string.movies_api_key)
    String apiKey;

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
    @BindView(R.id.tv_error_msg)
    TextView tvErrorMsg;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.layout_movie_details)
    View movieDetailsLayout;

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
        int movieId = getIntent().getIntExtra("movie_id", 0);
        new GetMovieDetails().execute(movieId);
    }

    class GetMovieDetails extends AsyncTask<Integer, Void, String> {
        @Override
        protected void onPreExecute() {
            if (!ConnectionUtils.haveNetworkConnection(context)) {
                Log.d(TAG, "onPreExecute: " + getString(R.string.no_internet_connection));
                showErrorView(true, getString(R.string.no_internet_connection));
                cancel(true);
            } else {
                showProgressBar(true);
            }
        }

        @Override
        protected String doInBackground(Integer... integers) {
            URL url = buildURL(integers[0]);
            Log.d(TAG, "URL: " + url.toString());
            HttpURLConnection urlConnection = null;
            String result = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();

                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    Scanner scanner = new Scanner(urlConnection.getInputStream());
                    scanner.useDelimiter("\\A");
                    result = scanner.next();
                } else {
                    Log.e(TAG, "doInBackground: Failed to get movie data");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String jsonResult) {
            if (!TextUtils.isEmpty(jsonResult) || jsonResult != null) {
                Log.d(TAG, "onPostExecute: Got something");
                Log.d(TAG, "onPostExecute: " + jsonResult);
                movie = JsonUtils.parseMovieDetails(jsonResult);

                showProgressBar(false);
                setMovieDetails(movie);
            }

        }
    }

    private void showProgressBar(boolean visible) {
        if (visible) {
            progressBar.setVisibility(View.VISIBLE);
            tvErrorMsg.setVisibility(View.GONE);
            movieDetailsLayout.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            tvErrorMsg.setVisibility(View.GONE);
            movieDetailsLayout.setVisibility(View.VISIBLE);
        }
    }

    private void showErrorView(boolean visible, String errorMsg) {
        if (visible) {
            progressBar.setVisibility(View.GONE);
            tvErrorMsg.setText(errorMsg);
            tvErrorMsg.setVisibility(View.VISIBLE);
            movieDetailsLayout.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            tvErrorMsg.setVisibility(View.GONE);
            movieDetailsLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setMovieDetails(Movie movie) {
        tvMovieName.setText(movie.getMovieTitle());
        tvReleaseDate.setText(movie.getMovieReleaseDate());
        tvRating.setText(String.valueOf(movie.getMovieRating()));
        tvMoviePlot.setText(movie.getMoviePlot());

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.placeholder);
        requestOptions.error(R.drawable.ic_round_error_24px);

        Glide.with(MovieDetailsActivity.this).load(movie.getMoviePosterUrl()).apply(requestOptions).transition(DrawableTransitionOptions.withCrossFade()).into(ivPosterImage);
    }

    private URL buildURL(int movieId) {
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(String.valueOf(movieId))
                .appendQueryParameter(API_KEY, apiKey)
                .build();

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
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
