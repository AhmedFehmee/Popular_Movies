package com.example.ahmed.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ahmed on 11/22/2015.
 */
public class movieAdapter extends ArrayAdapter<Movie> {

    Context context;
    int resource;

    public movieAdapter(Context context, int resource, ArrayList<Movie> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // convert convertview = design
        View convertview = convertView;
        ViewHolder holder;
        if (convertview == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertview = inflater.inflate(resource, parent, false);
            holder = new ViewHolder();
            holder.imageview = (ImageView) convertview.findViewById(R.id.img);
            convertview.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Movie movie = MainActivity.movies.get(position);
        Picasso.with(context).load(movie.getLargePoster()).resize(250, 250).into(holder.imageview);
        return convertview;
    }

    static class ViewHolder {
        public ImageView imageview;
    }

}
