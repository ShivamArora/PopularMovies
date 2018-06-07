package com.shivora.example.popularmovies.data;


public class Movie{
    private int movieId;
    private String movieTitle;
    private String moviePosterUrl;
    private float movieRating;

    public Movie() {
    }

    public Movie(int movieId, String movieTitle, String moviePosterUrl, float movieRating) {
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.moviePosterUrl = moviePosterUrl;
        this.movieRating = movieRating;
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


}
