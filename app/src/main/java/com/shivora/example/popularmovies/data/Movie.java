package com.shivora.example.popularmovies.data;


import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class Movie implements Parcelable{

    @SerializedName("id")
    private int movieId;
    @SerializedName("title")
    private String movieTitle;
    @SerializedName("poster_path")
    private String moviePosterUrl;
    @SerializedName("vote_average")
    private float movieRating;
    @SerializedName("overview")
    private String moviePlot;
    @SerializedName("release_date")
    private String movieReleaseDate;

    public Movie() {
    }

    public Movie(int movieId, String movieTitle, String moviePosterUrl, float movieRating, String moviePlot, String movieReleaseDate) {
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.moviePosterUrl = moviePosterUrl;
        this.movieRating = movieRating;
        this.moviePlot = moviePlot;
        this.movieReleaseDate = movieReleaseDate;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMoviePosterUrl() {
        return moviePosterUrl;
    }

    public void setMoviePosterUrl(String moviePosterUrl) {
        this.moviePosterUrl = moviePosterUrl;
    }

    public float getMovieRating() {
        return movieRating;
    }

    public void setMovieRating(float movieRating) {
        this.movieRating = movieRating;
    }

    public String getMoviePlot() {
        return moviePlot;
    }

    public void setMoviePlot(String moviePlot) {
        this.moviePlot = moviePlot;
    }

    public String getMovieReleaseDate() {
        return movieReleaseDate;
    }

    public void setMovieReleaseDate(String movieReleaseDate) {
        this.movieReleaseDate = movieReleaseDate;
    }

    //Contents below to make model a parcelable object

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(movieId);
        dest.writeString(movieTitle);
        dest.writeString(moviePosterUrl);
        dest.writeFloat(movieRating);
        dest.writeString(moviePlot);
        dest.writeString(movieReleaseDate);
    }

    //CREATOR
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>(){
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    //De-parcel the object
    public Movie(Parcel in){
        movieId = in.readInt();
        movieTitle = in.readString();
        moviePosterUrl = in.readString();
        movieRating = in.readFloat();
        moviePlot = in.readString();
        movieReleaseDate = in.readString();
    }
}
