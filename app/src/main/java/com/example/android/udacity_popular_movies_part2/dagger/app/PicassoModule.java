package com.example.android.udacity_popular_movies_part2.dagger.app;

import android.content.Context;

import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;

@Module(includes = ContextModule.class)
public class PicassoModule {

    @Provides
    @MovieApplicationScope
    public Picasso picasso(Context context) {
        return new Picasso.Builder(context).build();
    }
}
