package com.shivora.example.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.shivora.example.popularmovies.R;
import com.shivora.example.popularmovies.data.Movie;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.shivora.example.popularmovies.utils.NetworkingUtils.BASE_URL_POSTER;
import static com.shivora.example.popularmovies.utils.NetworkingUtils.QUALITY_500;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {

    public interface MovieItemClickListener{
        void onMovieItemClick(int position);
    }

    private List<Movie> moviesList;
    private final MovieItemClickListener movieItemClickListener;
    public MoviesAdapter(List<Movie> moviesList,MovieItemClickListener movieItemClickListener) {
        this.moviesList = moviesList;
        this.movieItemClickListener = movieItemClickListener;
    }

    @NonNull
    @Override
    public MoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int itemLayoutId = R.layout.movie_item;
        boolean shouldAttachToParentImmediately = false;

        View listItem = inflater.inflate(itemLayoutId,parent,shouldAttachToParentImmediately);

        return new MoviesViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    public void setMoviesList(List<Movie> moviesList){
        this.moviesList = moviesList;
        notifyDataSetChanged();
    }

    class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.iv_movie_icon)
        ImageView ivMovieIcon;

        public MoviesViewHolder(View itemView) {
            super(itemView);
            this.itemView.setOnClickListener(this);
            ButterKnife.bind(this,itemView);
        }

        public void bind(int position){
            Movie movie = moviesList.get(position);
            this.itemView.setId(movie.getMovieId());
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.placeholder);
            requestOptions.error(R.drawable.error_placeholder);

            Glide.with(this.itemView).load(BASE_URL_POSTER+QUALITY_500+movie.getMoviePosterUrl()).apply(requestOptions).into(ivMovieIcon);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            movieItemClickListener.onMovieItemClick(position);
        }
    }
}
