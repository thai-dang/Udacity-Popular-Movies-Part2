package com.example.android.udacity_popular_movies_part2.utils;

import com.example.android.udacity_popular_movies_part2.R;

public class URLUtils {

    public static String makeImageURL(String posterPath) {
        return Constants.IMAGE_URL + "/w342" + posterPath + "?api_key?=" + R.string.my_moviedb_key;
    }

    public static String makeThumbnailURL(String thumbnailId) {
        return Constants.YT_THUMB_URL.concat(thumbnailId).concat("/hqdefault.jpg");
    }
}
