package com.example.android.udacity_popular_movies_part2.dagger.activity.list;

import android.content.Context;

import com.example.android.udacity_popular_movies_part2.MovieListPresenter;
import com.example.android.udacity_popular_movies_part2.MovieListView;
import com.example.android.udacity_popular_movies_part2.dagger.app.activity.home.MovieListScope;
import com.example.android.udacity_popular_movies_part2.retrofit.MovieService;

import dagger.Module;
import dagger.Provides;

@MovieListScope
@Module
public class MovieListModule {

    private final MovieListView movieListView;

    public MovieListModule(MovieListView movieListView) {
        this.movieListView = movieListView;
    }

    @Provides
    @MovieListScope
    public MovieListView movieListView() {
        return movieListView;
    }

    @Provides
    @MovieListScope
    public MovieListPresenter movieListPresenter(MovieListView movieListView, MovieService movieService, Context context) {
        return new MovieListPresenter(movieListView, movieService, context);
    }


}
