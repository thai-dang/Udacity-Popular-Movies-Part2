package com.example.android.udacity_popular_movies_part2;

import com.example.android.udacity_popular_movies_part2.utils.URLUtils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class URLUtilsUnitTest {

    @Test
    public void thumbnailURL_isCorrect() throws Exception {
        assertEquals(URLUtils.makeThumbnailURL("hello"), "http://img.youtube.com/vi/hello/hqdefault.jpg");
    }

    @Test
    public void imageURL_isCorrect() throws Exception {
        assertEquals(URLUtils.makeImageURL("hello"), "http://image.tmdb.org/t/p/w342hello?api_key?=".concat(BuildConfig.API_KEY));
    }
}