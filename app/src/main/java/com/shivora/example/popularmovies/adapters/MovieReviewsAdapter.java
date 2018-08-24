package com.shivora.example.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shivora.example.popularmovies.R;
import com.shivora.example.popularmovies.data.MovieReviewsList;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.MovieReviewsViewHolder> {
    List<MovieReviewsList.MovieReview> movieReviewList;

    public MovieReviewsAdapter(List<MovieReviewsList.MovieReview> movieReviewsList) {
        this.movieReviewList = movieReviewsList;
    }

    @NonNull
    @Override
    public MovieReviewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View reviewItem = layoutInflater.inflate(R.layout.movie_review_item,parent,false);
        return new MovieReviewsViewHolder(reviewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieReviewsViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return movieReviewList.size();
    }

    public class MovieReviewsViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_review_author)
        TextView tvReviewAuthor;
        @BindView(R.id.tv_review_body)
        TextView tvReviewBody;

        public MovieReviewsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void bind(int position){
            String author = movieReviewList.get(position).getAuthor();
            String body = movieReviewList.get(position).getContent();
            tvReviewAuthor.setText(" - "+author);
            tvReviewBody.setText(body);
        }
    }
}
