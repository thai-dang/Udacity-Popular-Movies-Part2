package com.example.android.udacity_popular_movies_part2.dagger.activity.details;

import com.example.android.udacity_popular_movies_part2.MovieDetailActivity;
import com.example.android.udacity_popular_movies_part2.MovieDetailFragment;
import com.example.android.udacity_popular_movies_part2.dagger.app.MovieApplicationComponent;

import dagger.Component;

@MovieDetailsScope
@Component(dependencies = MovieApplicationComponent.class)
public interface MovieDetailsActivityComponent {

    void injectMovieDetailsActivity(MovieDetailActivity movieDetailActivity);

    void injectMovieDetailsFragment(MovieDetailFragment movieDetailFragment);
}
