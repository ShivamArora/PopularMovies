package com.shivora.example.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shivora.example.popularmovies.R;
import com.shivora.example.popularmovies.data.MovieTrailersList.MovieTrailer;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieTrailersAdapter extends RecyclerView.Adapter<MovieTrailersAdapter.MovieTrailersViewHolder> {

    public interface MovieTrailerClickListener{
        void onMovieTrailerClick(String key);
    }

    List<MovieTrailer> movieTrailers;
    MovieTrailerClickListener mMovieTrailerClickListener;
    public MovieTrailersAdapter(List<MovieTrailer> movieTrailers,MovieTrailerClickListener movieTrailerClickListener){
        this.movieTrailers = movieTrailers;
        this.mMovieTrailerClickListener = movieTrailerClickListener;
    }

    @NonNull
    @Override
    public MovieTrailersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int itemLayoutId = R.layout.movie_trailer_item;
        boolean shouldAttachToParentImmediately = false;

        View itemView = inflater.inflate(itemLayoutId,parent,shouldAttachToParentImmediately);

        return new MovieTrailersViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieTrailersViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return movieTrailers.size();
    }

    public void setMovieTrailers(List<MovieTrailer> movieTrailers){
        this.movieTrailers = movieTrailers;
        notifyDataSetChanged();
    }

    class MovieTrailersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.iv_trailer_thummbnail)
        ImageView ivThumbnail;
        @BindView(R.id.iv_icon_play)
        ImageView ivIconPlay;
        public MovieTrailersViewHolder(View itemView) {
            super(itemView);
            this.itemView.setOnClickListener(this);
            ButterKnife.bind(this,itemView);
        }

        public void bind(int position){
            Log.d("Adapter", "bind: "+movieTrailers.get(position).getMovieVideoKey());
            String videoKey = movieTrailers.get(position).getMovieVideoKey();
            Glide.with(this.itemView).load("https://img.youtube.com/vi/"+videoKey+"/default.jpg").into(ivThumbnail);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mMovieTrailerClickListener.onMovieTrailerClick(movieTrailers.get(position).getMovieVideoKey());
        }
    }
}
