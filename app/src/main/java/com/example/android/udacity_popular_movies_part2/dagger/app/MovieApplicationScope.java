package com.example.android.udacity_popular_movies_part2.dagger.app;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

@Scope
@Retention(RetentionPolicy.CLASS)
public @interface MovieApplicationScope {
}
