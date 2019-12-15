package com.example.android.udacity_popular_movies_part2;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.example.android.udacity_popular_movies_part2.adapters.ReviewAdapter;
import com.example.android.udacity_popular_movies_part2.adapters.TrailerAdapter;
import com.example.android.udacity_popular_movies_part2.application.MovieApplication;
import com.example.android.udacity_popular_movies_part2.dagger.activity.details.DaggerMovieDetailsActivityComponent;
import com.example.android.udacity_popular_movies_part2.dagger.activity.details.MovieDetailsActivityComponent;
import com.example.android.udacity_popular_movies_part2.models.MovieModel;
import com.example.android.udacity_popular_movies_part2.retrofit.MovieService;
import com.example.android.udacity_popular_movies_part2.utils.Constants;
import com.example.android.udacity_popular_movies_part2.utils.NetworkUtils;
import com.example.android.udacity_popular_movies_part2.utils.RecyclerClickListener;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

public class MovieDetailFragment extends Fragment {

    MovieModel movieModel;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.titleView)
    TextView titleView;
    @BindView(R.id.textview_vote_average)
    TextView rating;
    @BindView(R.id.overview)
    TextView overview;
    @BindView(R.id.textview_release_date)
    TextView releaseText;
    @BindView(R.id.trailersRecyclerView)
    RecyclerView trailersRecyclerView;
    @BindView(R.id.reviewsRecyclerView)
    RecyclerView reviewsRecyclerView;
    @BindView(R.id.noReviewView)
    TextView noReviewView;
    @BindView(R.id.noTrailerView)
    TextView noTrailerView;
    @BindView(R.id.extras)
    LinearLayout extraLayout;
    @Inject
    ReviewAdapter reviewAdapter;
    @Inject
    TrailerAdapter trailerAdapter;
    @Inject
    Picasso picasso;
    @Inject
    Realm realm;
    @Inject
    MovieService movieService;

    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments().containsKey("movie")) {
            movieModel = getArguments().getParcelable("movie");
            assert movieModel != null;
        }

        MovieDetailsActivityComponent movieDetailsActivityComponent = DaggerMovieDetailsActivityComponent.builder()
                .movieApplicationComponent(MovieApplication.get(getActivity()).getMovieApplicationComponent())
                .build();

        movieDetailsActivityComponent.injectMovieDetailsFragment(this);

        if (NetworkUtils.isNetworkAvailable(getActivity())) {
            fetchReviews(movieModel.getId());
            fetchTrailers(movieModel.getId());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);
        ButterKnife.bind(this,rootView);

        titleView.setText(movieModel.getoriginal_title());

        picasso.load(Constants.IMAGE_URL + "/w342" + movieModel.getposter_path() + "?api_key?=" + R.string.my_moviedb_key).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(imageView);

        rating.setText(Float.toString(movieModel.getvote_average()).concat(" / 10"));

        overview.setText(movieModel.getOverview());
        releaseText.setText(movieModel.getrelease_date());

        if (!NetworkUtils.isNetworkAvailable(getActivity()))
            extraLayout.setVisibility(View.INVISIBLE);

        LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(getContext());

        trailersRecyclerView.setLayoutManager(trailerLayoutManager);
        reviewsRecyclerView.setLayoutManager(reviewLayoutManager);

        trailersRecyclerView.setAdapter(trailerAdapter);
        reviewsRecyclerView.setAdapter(reviewAdapter);

        trailersRecyclerView.addOnItemTouchListener(new RecyclerClickListener(getContext(), (view, position) -> {
            String url = "https://www.youtube.com/watch?v=".concat(trailerAdapter.get(position).getKey());
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }));

        reviewsRecyclerView.addOnItemTouchListener(new RecyclerClickListener(getContext(), (view, position) -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(reviewAdapter.get(position).getUrl()));
            startActivity(i);
        }));
        return rootView;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.share).setVisible(true);
        MenuItem item = menu.findItem(R.id.favorite);
        item.setVisible(true);
        item.setIcon(!isFavorite() ? R.drawable.remove_favorite : R.drawable.add_favorite);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.share:
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_SUBJECT, movieModel.getoriginal_title());
                share.putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/watch?v=".concat(trailerAdapter.get(0).getKey()));
                startActivity(Intent.createChooser(share, "Share Trailer!"));
                break;

            case R.id.favorite:
                if (realm.isInTransaction())
                    realm.cancelTransaction();
                if (!isFavorite()) {
                    realm.beginTransaction();
                    item.setIcon(R.drawable.add_favorite);
                    realm.copyToRealm(movieModel);
                    realm.commitTransaction();
                } else {
                    realm.beginTransaction();
                    item.setIcon(R.drawable.remove_favorite);
                    realm.where(MovieModel.class).contains("id", movieModel.getId()).findFirst().deleteFromRealm();
                    realm.commitTransaction();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isFavorite(){
        return realm.where(MovieModel.class).contains("id", movieModel.getId()).findAll().size() != 0;
    }

    private void fetchReviews(final String id) {
        movieService.loadReviews(id, getString(R.string.my_moviedb_key))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(reviews -> {
                    reviewAdapter.addAll(reviews.results);
                    if (reviewAdapter.getData().isEmpty()) {
                        reviewsRecyclerView.setVisibility(View.INVISIBLE);
                        noReviewView.setVisibility(View.VISIBLE);
                    }
                });

    }

    private void fetchTrailers(final String id) {
        movieService.loadTrailers(id, getString(R.string.my_moviedb_key))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trailers -> {
                    trailerAdapter.addAll(trailers.results);
                    if (trailerAdapter.getData().isEmpty()) {
                        trailersRecyclerView.setVisibility(View.INVISIBLE);
                        noTrailerView.setVisibility(View.VISIBLE);
                    }
                });
    }
}
