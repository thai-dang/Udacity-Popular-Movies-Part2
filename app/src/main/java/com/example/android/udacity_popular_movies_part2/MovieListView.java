package com.example.android.udacity_popular_movies_part2;

import com.example.android.udacity_popular_movies_part2.models.MovieModel;

import java.util.ArrayList;

public interface MovieListView {
    void setProgressBarVisibility(int visibility);
    void startMovieDetailFragment(MovieDetailFragment movieDetailFragment);
    void startMovieDetailActivity(MovieModel movieModel);
    void setMenuItemChecked(int id);
    void showMovies(ArrayList<MovieModel> movieModels);
}
