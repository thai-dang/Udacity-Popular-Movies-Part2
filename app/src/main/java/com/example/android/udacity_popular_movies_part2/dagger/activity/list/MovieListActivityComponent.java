package com.example.android.udacity_popular_movies_part2.dagger.activity.list;

import com.example.android.udacity_popular_movies_part2.MovieListActivity;
import com.example.android.udacity_popular_movies_part2.dagger.app.ContextModule;
import com.example.android.udacity_popular_movies_part2.dagger.app.MovieApplicationComponent;
import com.example.android.udacity_popular_movies_part2.dagger.app.activity.home.MovieListScope;

import dagger.Component;

@MovieListScope
@Component(modules = {MovieListModule.class, ContextModule.class}, dependencies = MovieApplicationComponent.class)
public interface MovieListActivityComponent {

    void injectMovieListActivity(MovieListActivity movieListActivity);

}
