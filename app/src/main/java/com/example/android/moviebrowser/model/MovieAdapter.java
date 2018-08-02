package com.example.android.moviebrowser.model;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.moviebrowser.R;
import com.example.android.moviebrowser.controller.DetailActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private Context context;
    private List<Movie> movies = new ArrayList<>();

    public MovieAdapter(Context context) {
        this.context = context;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForGridItem = R.layout.recyler_item;
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(layoutIdForGridItem, parent, false);
        MovieViewHolder viewHolder = new MovieViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return this.movies.size();
    }

    public void addNewItems(List<Movie> movies) {
        int previousContentSize = this.movies.size();
        this.movies.clear();
        this.movies.addAll(movies);
        notifyItemRangeRemoved(0, previousContentSize);
        notifyItemRangeInserted(0, this.movies.size());
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        private ImageView gridImageView;
        private TextView nameTextView;
        private TextView budgetTextView;
        private int id;
        private String posterPath;

        public MovieViewHolder(View itemView) {
            super(itemView);
            gridImageView = itemView.findViewById(R.id.movie_poster_iv);
            nameTextView = itemView.findViewById(R.id.movie_title_tv);
            budgetTextView = itemView.findViewById(R.id.movie_budget_tv);
        }

        void bind(int listIndex) {
            Movie movie = MovieAdapter.this.movies.get(listIndex);
            this.posterPath = movie.getPosterPath();
            this.id = movie.getId();
            Picasso.with(MovieAdapter.this.context).load(this.posterPath).resize(500,750).centerCrop().onlyScaleDown().into(gridImageView);
            nameTextView.setText(movie.getTitle());
            budgetTextView.setText(movie.getBudget());

            View.OnClickListener cl = new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MovieAdapter.this.context, DetailActivity.class);
                    intent.putExtra("id", MovieViewHolder.this.id);
                    intent.putExtra("poster_path", MovieViewHolder.this.posterPath);
                    MovieAdapter.this.context.startActivity(intent);
                }
            };
            itemView.setOnClickListener(cl);
        }
    }
}