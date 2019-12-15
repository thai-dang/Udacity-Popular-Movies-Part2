package com.example.android.udacity_popular_movies_part2.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.example.android.udacity_popular_movies_part2.R;
import com.example.android.udacity_popular_movies_part2.models.MovieModel;
import com.example.android.udacity_popular_movies_part2.utils.URLUtils;

import java.util.ArrayList;

import javax.inject.Inject;

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Picasso picasso;
    private ArrayList<MovieModel> data;

    @Inject
    MovieAdapter(Picasso picasso) {
        this.picasso = picasso;
        this.data = new ArrayList<>();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.grid_image, parent, false);
        viewHolder = new MyItemHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        String imageURL = URLUtils.makeImageURL(data.get(position).getposter_path());
        picasso.load(imageURL).into(((MyItemHolder) holder).imageView);


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addAll(ArrayList<MovieModel> list) {
        data.clear();
        data.addAll(list);
        notifyDataSetChanged();
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder {
        ImageView imageView;


        MyItemHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.listImage);
        }

    }

}