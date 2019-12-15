package com.example.android.udacity_popular_movies_part2.application;

import android.app.Activity;
import android.app.Application;

import com.example.android.udacity_popular_movies_part2.dagger.app.ContextModule;
import com.example.android.udacity_popular_movies_part2.dagger.app.DaggerMovieApplicationComponent;
import com.example.android.udacity_popular_movies_part2.dagger.app.MovieApplicationComponent;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MovieApplication extends Application {

    private MovieApplicationComponent movieApplicationComponent;

    public static MovieApplication get(Activity activity) {
        return (MovieApplication) activity.getApplication();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(getApplicationContext());
        RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(config);

        movieApplicationComponent = DaggerMovieApplicationComponent
                .builder()
                .contextModule(new ContextModule(this))
                .build();

    }

    public MovieApplicationComponent getMovieApplicationComponent() {
        return movieApplicationComponent;
    }

}
