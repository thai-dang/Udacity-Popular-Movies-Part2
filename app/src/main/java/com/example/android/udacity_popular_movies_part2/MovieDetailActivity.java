package com.example.android.udacity_popular_movies_part2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.picasso.Picasso;
import com.example.android.udacity_popular_movies_part2.application.MovieApplication;
import com.example.android.udacity_popular_movies_part2.dagger.activity.details.DaggerMovieDetailsActivityComponent;
import com.example.android.udacity_popular_movies_part2.dagger.activity.details.MovieDetailsActivityComponent;
import com.example.android.udacity_popular_movies_part2.models.MovieModel;

import javax.inject.Inject;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity {

    @Inject
    Picasso picasso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        MovieModel movieModel =  getIntent().getParcelableExtra("movie");

        MovieDetailsActivityComponent movieDetailsActivityComponent = DaggerMovieDetailsActivityComponent.builder()
                .movieApplicationComponent(MovieApplication.get(this).getMovieApplicationComponent())
                .build();

        movieDetailsActivityComponent.injectMovieDetailsActivity(this);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable("movie", movieModel);
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                supportFinishAfterTransition();
                super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
