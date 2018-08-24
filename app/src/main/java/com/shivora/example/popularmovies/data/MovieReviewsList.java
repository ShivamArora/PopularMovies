package com.shivora.example.popularmovies.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieReviewsList {
    @SerializedName("results")
    List<MovieReview> movieReviewsList;

    public List<MovieReview> getMovieReviewsList() {
        return movieReviewsList;
    }

    public void setMovieReviewsList(List<MovieReview> movieReviewsList) {
        this.movieReviewsList = movieReviewsList;
    }

    public class MovieReview{
        @SerializedName("author")
        String author;
        @SerializedName("content")
        String content;

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
