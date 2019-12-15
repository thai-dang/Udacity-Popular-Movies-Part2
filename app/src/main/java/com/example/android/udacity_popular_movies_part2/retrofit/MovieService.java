package com.example.android.udacity_popular_movies_part2.retrofit;

import com.example.android.udacity_popular_movies_part2.models.MovieModel;
import com.example.android.udacity_popular_movies_part2.models.ReviewModel;
import com.example.android.udacity_popular_movies_part2.models.TrailerModel;

import java.util.ArrayList;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface MovieService {


    @GET("/3/movie/{sort}")
    Observable<Movies> loadMovies(@Path("sort") String sort, @Query("api_key") String api_key);

    @GET("/3/movie/{id}/videos")
    Observable<Trailers> loadTrailers(@Path("id") String id, @Query("api_key") String api_key);

    @GET("/3/movie/{id}/reviews")
    Observable<Reviews> loadReviews(@Path("id") String id, @Query("api_key") String api_key);


    class Movies {
        public ArrayList<MovieModel> results;
    }

    class Reviews {
        public ArrayList<ReviewModel> results;
    }

    class Trailers {
        public ArrayList<TrailerModel> results;
    }
}
