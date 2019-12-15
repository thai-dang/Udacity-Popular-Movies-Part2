package com.example.android.udacity_popular_movies_part2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.android.udacity_popular_movies_part2.models.MovieModel;
import com.example.android.udacity_popular_movies_part2.retrofit.MovieService;
import com.example.android.udacity_popular_movies_part2.utils.NetworkUtils;
import com.example.android.udacity_popular_movies_part2.utils.SortType;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.example.android.udacity_popular_movies_part2.utils.SortType.FAVORITE;
import static com.example.android.udacity_popular_movies_part2.utils.SortType.POPULAR;
import static com.example.android.udacity_popular_movies_part2.utils.SortType.RATED;

public class MovieListPresenter {

    private MovieListView movieListView;

    private ArrayList<MovieModel> popularList = new ArrayList<>();
    private ArrayList<MovieModel> ratedList = new ArrayList<>();
    private ArrayList<MovieModel> favoriteList = new ArrayList<>();

    private MovieService movieService;
    private boolean twoPane;

    private NetworkReceiver networkReceiver;
    private SortType SORT_BY;

    private Realm realm;

    private Context context;

    private boolean disconnected;


    @Inject
    public MovieListPresenter(MovieListView movieListView, MovieService movieService, Context context) {
        this.movieListView = movieListView;
        this.movieService = movieService;
        this.context = context;
        realm = Realm.getDefaultInstance();
    }

    public void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        networkReceiver = new NetworkReceiver();
        context.registerReceiver(networkReceiver, intentFilter);
    }

    public void fetchMovies(final String sort) {
        movieService
                .loadMovies(sort, context.getString(R.string.my_moviedb_key))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movies -> {
                    movieListView.setProgressBarVisibility(View.INVISIBLE);
                    if (sort.equals("popular")) {
                        popularList.addAll(movies.results);
                    } else {
                        ratedList.addAll(movies.results);
                    }
                    if (twoPane && popularList.size() != 0) {
                        Bundle arguments = new Bundle();
                        arguments.putParcelable("movie", popularList.get(0));
                        MovieDetailFragment fragment = new MovieDetailFragment();
                        fragment.setArguments(arguments);
                        movieListView.startMovieDetailFragment(fragment);
                    }
                    showMovies();
                });
    }

    public void getFavorites() {
        RealmResults<MovieModel> realmResults = realm.where(MovieModel.class).findAll();
        favoriteList.clear();
        favoriteList.addAll(realmResults);
        if (SORT_BY == FAVORITE)
            movieListView.showMovies(favoriteList);
    }

    public void setTwoPane() {
        twoPane = true;
    }

    public void unregisterReceiver() {
        context.unregisterReceiver(networkReceiver);
    }

    public void handleRecyclerItemClicked(int position) {
        MovieModel movieModel;
        switch (SORT_BY) {
            case POPULAR:
                movieModel = popularList.get(position);
                break;
            case RATED:
                movieModel = ratedList.get(position);
                break;
            case FAVORITE:
                movieModel = favoriteList.get(position);
                break;
            default:
                movieModel = null;
                break;
        }

        if (twoPane) {
            Bundle arguments = new Bundle();
            arguments.putParcelable("movie", movieModel);
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            movieListView.startMovieDetailFragment(fragment);
        } else {

            movieListView.startMovieDetailActivity(movieModel);
        }
    }

    public void fetchAllMovies() {
        if (NetworkUtils.isNetworkAvailable(context)) {
            fetchMovies("popular");
            fetchMovies("top_rated");
        }
    }

    public void setMenuItemChecked() {
        int id;
        switch (SORT_BY) {
            case POPULAR:
                id = R.id.action_sort_by_popularity;
                break;
            case RATED:
                id = R.id.action_sort_by_rating;
                break;
            case FAVORITE:
                id = R.id.action_sort_by_favorite;
                break;
            default:
                id = R.id.action_sort_by_popularity;
        }
        movieListView.setMenuItemChecked(id);
    }

    public void putInBundle(Bundle bundle) {
        bundle.putParcelableArrayList("POPULAR", popularList);
        bundle.putParcelableArrayList("RATED", ratedList);
        bundle.putSerializable("SORT_BY", SORT_BY);
    }

    public void extractFromBundle(Bundle bundle) {
        if (popularList.size() == 0)
            popularList.addAll(bundle.getParcelableArrayList("POPULAR"));
        if (ratedList.size() == 0)
            ratedList.addAll(bundle.getParcelableArrayList("RATED"));
        SORT_BY = (SortType) bundle.get("SORT_BY");
    }

    public void showMovies() {
        switch (SORT_BY) {
            case POPULAR:
                movieListView.showMovies(popularList);
                break;
            case RATED:
                movieListView.showMovies(ratedList);
                break;
            case FAVORITE:
                movieListView.showMovies(favoriteList);
                break;
            default:
                movieListView.showMovies(popularList);
        }
    }

    public void handleActivityInstance(Bundle bundle) {
        if (bundle != null) {
            extractFromBundle(bundle);
            movieListView.setProgressBarVisibility(View.INVISIBLE);
            showMovies();
        } else {
            fetchAllMovies();
            SORT_BY = POPULAR;
        }
        getFavorites();

    }

    public void addRealmListener() {
        realm.addChangeListener(realm1 -> getFavorites());
    }

    public void onOptionsItemSelected(int id) {
        if (id == R.id.action_sort_by_favorite)
            movieListView.setProgressBarVisibility(View.INVISIBLE);
        else if ((id == R.id.action_sort_by_popularity) || (id == R.id.action_sort_by_rating)) {
            if (popularList.size() == 0 && ratedList.size() == 0)
                movieListView.setProgressBarVisibility(View.VISIBLE);
        }
        switch (id) {
            case R.id.action_sort_by_rating:
                SORT_BY = RATED;
                break;
            case R.id.action_sort_by_popularity:
                SORT_BY = POPULAR;
                break;
            case R.id.action_sort_by_favorite:
                SORT_BY = FAVORITE;
                getFavorites();
                break;
        }
        showMovies();
    }

    public class NetworkReceiver extends BroadcastReceiver {
        public NetworkReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("TAG", disconnected + "");
            if (disconnected)
                if (NetworkUtils.isNetworkAvailable(context) && popularList.size() == 0) {
                    fetchMovies("popular");
                    fetchMovies("top_rated");
                }
            disconnected = !disconnected;
        }
    }
}
