package com.wernerraubenheimer.popularmovies;

import android.content.Context;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by Werner on 2015/08/11.
 * I used example of Creating custom ArrayAdapter tutorial
 * Found other example code here:
 * http://javatechig.com/android/android-gridview-example-building-image-gallery-in-android
 *
 **/
public class GridViewAdapter extends ArrayAdapter<Movie> {

    private static final String LOG_TaG = GridViewAdapter.class.getSimpleName();
    private Context context;

    /**This is my own custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the List is the data I want
     * to populate into the lists
     *
     * @param context        The current context. Used to inflate the layout file.
     * @param movieList      A List of Movie objects to display in a list
     */
    public GridViewAdapter(Context context, ArrayList movieList) {


        // Here, I initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for an ImageView only, the adapter is not
        // going to use this second argument, so it can be any value. Here, I used 0.
        super(context, 0, movieList);
        this.context = context; //Get the context for the Picaso method call
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Gets the AndroidFlavor object from the ArrayAdapter at the appropriate position
        Movie movie = getItem(position);

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_movie, parent, false);
        }

        if (URLUtil.isValidUrl(movie.getPosterPath())) {
            ImageView imageView = (ImageView)convertView.findViewById(R.id.grid_item_movie_imageview);
            Picasso.with(parent.getContext()).load(movie.getPosterPath()).into(imageView);
        }


        return  convertView;

    }

}
