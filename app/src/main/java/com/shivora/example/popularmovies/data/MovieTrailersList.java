package com.shivora.example.popularmovies.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieTrailersList {
    @SerializedName("results")
    List<MovieTrailer> movieTrailerList;

    public List<MovieTrailer> getMovieTrailerList() {
        return movieTrailerList;
    }

    public void setMovieTrailerList(List<MovieTrailer> movieTrailerList) {
        this.movieTrailerList = movieTrailerList;
    }

    public class MovieTrailer{
        @SerializedName("key")
        String movieVideoKey;
        @SerializedName("type")
        String videoType;

        public String getMovieVideoKey() {
            return movieVideoKey;
        }

        public void setMovieVideoKey(String movieVideoKey) {
            this.movieVideoKey = movieVideoKey;
        }

        public String getVideoType() {
            return videoType;
        }

        public void setVideoType(String videoType) {
            this.videoType = videoType;
        }

        public boolean isTrailer(){
            return (videoType.equals("Trailer")||videoType.equals("Teaser"));
        }
    }
}
