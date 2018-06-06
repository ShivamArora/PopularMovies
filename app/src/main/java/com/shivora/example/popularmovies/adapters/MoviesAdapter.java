package com.shivora.example.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.shivora.example.popularmovies.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {
    private int count;
    public MoviesAdapter(int count) {
        this.count = count;
    }

    @NonNull
    @Override
    public MoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int itemLayoutId = R.layout.movie_list_item;
        boolean shouldAttachToParentImmediately = false;

        View listItem = inflater.inflate(itemLayoutId,parent,shouldAttachToParentImmediately);

        MoviesViewHolder itemViewHolder = new MoviesViewHolder(listItem);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return count;
    }

    class MoviesViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.iv_movie_icon)
        ImageView ivMovieIcon;
        @BindView(R.id.tv_movie_title)
        TextView tvMovieTitle;
        @BindView(R.id.ratingBar)
        RatingBar ratingBar;

        public MoviesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void bind(int position){
            ivMovieIcon.setImageResource(R.mipmap.ic_launcher_round);
            tvMovieTitle.setText("Movie Title");
            ratingBar.setRating(4);
        }
    }
}
