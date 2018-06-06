package com.shivora.example.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shivora.example.popularmovies.adapters.MoviesAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DiscoverMoviesActivity extends AppCompatActivity {

    @BindView(R.id.recycler_view_movies)
    RecyclerView recyclerView;
    @BindView(R.id.tv_empty_list)
    TextView tvEmptyList;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    MoviesAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_movies);
        ButterKnife.bind(this);

        //GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new MoviesAdapter(10);

        recyclerView.setAdapter(adapter);
    }
}
