package com.wernerraubenheimer.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private final String LOG_TAG = DetailActivity.class.getSimpleName();

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);


        Movie mMovieItem = getActivity().getIntent().getParcelableExtra("movie");
        if (mMovieItem != null) {
            TextView titleView = (TextView)rootView.findViewById(R.id.originalTitle);
            titleView.setText(mMovieItem.getOriginalTitle());

            TextView releaseDateView = (TextView)rootView.findViewById(R.id.releaseDateView);
            releaseDateView.setText("Release date: \n" + mMovieItem.getReleaseDate());

            TextView averageScoreView = (TextView)rootView.findViewById(R.id.averageScoreRating);
            averageScoreView.setText("Vote average: \n" + mMovieItem.getVoteAverage());

            TextView overView = (TextView)rootView.findViewById(R.id.overView);
            overView.setText("Overview \n" + mMovieItem.getOverView());

            //Check if it is a Valid URL - http://stackoverflow.com/questions/4905075/how-to-check-if-url-is-valid-in-android
            if(Patterns.WEB_URL.matcher(mMovieItem.getPosterPath()).matches()) {
                ImageView imageView = (ImageView) rootView.findViewById(R.id.posterImageView);
                Picasso.with(rootView.getContext()).load(mMovieItem.getPosterPath()).into(imageView);
            }
        }
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_settings){
            Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
