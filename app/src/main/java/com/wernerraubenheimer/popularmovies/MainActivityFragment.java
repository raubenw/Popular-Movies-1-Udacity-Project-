package com.wernerraubenheimer.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private GridViewAdapter mMovieAdapter;
    private ArrayList<Movie> movieList;
    private Movie[] movieResults = {new Movie()};
    private final String PARCELABLE_STATE_MOVIES = "movies";
    private final String STATE_SORT_BY = "sort_by";
    SharedPreferences shared_preferences;
    SharedPreferences.Editor shared_preferences_editor;
    private String sort_by_preference;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Toast.makeText(getActivity(), "In onCreate()",Toast.LENGTH_LONG).show();
        if (savedInstanceState == null || !savedInstanceState.containsKey(PARCELABLE_STATE_MOVIES)) {
            updateMovies();
            movieList = new ArrayList<>(Arrays.asList(movieResults));
        } else {
            movieList = savedInstanceState.getParcelableArrayList(PARCELABLE_STATE_MOVIES);
            sort_by_preference = savedInstanceState.getString(STATE_SORT_BY);
        }
        mMovieAdapter = new GridViewAdapter(getActivity(), movieList);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(PARCELABLE_STATE_MOVIES, movieList);
        outState.putString(STATE_SORT_BY, sort_by_preference);
        super.onSaveInstanceState(outState);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView movie_grid_view = (GridView)rootView.findViewById(R.id.grid_view_movie);
        movie_grid_view.setAdapter(mMovieAdapter);

        //Add an onItemClickListener to the poster icons
        movie_grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                   @Override
                                                   public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                       Movie mMovieItem = mMovieAdapter.getItem(position);
                                                       Intent detailActivity = new Intent(getActivity(), DetailActivity.class);
                                                       detailActivity.putExtra("movie", mMovieItem);
                                                       startActivity(detailActivity);
                                                   }
                                               }
        );

        return rootView;
    }

    private void updateMovies() {
        FetchMoviesTask fetchMovies = new FetchMoviesTask();
        shared_preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sort_by_preference = shared_preferences.getString(getString(R.string.pref_sort_by_key),
                getString(R.string.pref_sort_by_default_value));
        fetchMovies.execute(sort_by_preference);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Toast.makeText(getActivity(), "In onResume()",Toast.LENGTH_LONG).show();
        shared_preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String newSortOrder = shared_preferences.getString(getString(R.string.pref_sort_by_key),
                getString(R.string.pref_sort_by_default_value));
        if(!newSortOrder.equalsIgnoreCase(sort_by_preference)) {
            sort_by_preference = newSortOrder;
            updateMovies();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //Toast.makeText(getActivity(), "In onStart()",Toast.LENGTH_LONG).show();
    }



    public class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private final String MOVIE_BASE_URL = getString(R.string.movie_base_url);
        private final String PARAM_SORT_BY = getString(R.string.param_sort_by);
        private final String PARAM_API_KEY = getString(R.string.param_api_key);

        /**
         * Take the String representing the complete movie list in JSON Format and
         * pull out the data o construct the Strings needed for the wireframes.
        */
        private Movie[] getMovieDataFromJson(String movieJsonStr) throws JSONException {

            // Values returned by API
            final String TMDB_MOVIE_LIST = "results";
            final String TMDB_ORIGINAL_TITLE = "original_title";
            final String TMDB_POSTER_PATH = "poster_path";
            final String TMDB_OVERVIEW = "overview";
            final String TMDB_VOTE_AVERAGE = "vote_average";
            final String TMDB_RELEASE_DATE = "release_date";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(TMDB_MOVIE_LIST);

            //no data, so return nothing
            if (movieArray.length() == 0)
                return null;

            //Update the global private variable
            movieResults = new Movie[movieArray.length()];

            for (int i = 0; i < movieResults.length; i++) {
                movieResults[i] = new Movie(movieArray.getJSONObject(i).getString(TMDB_ORIGINAL_TITLE),
                                            movieArray.getJSONObject(i).getString(TMDB_POSTER_PATH),
                                            movieArray.getJSONObject(i).getString(TMDB_OVERVIEW),
                                            movieArray.getJSONObject(i).getString(TMDB_VOTE_AVERAGE),
                                            movieArray.getJSONObject(i).getString(TMDB_RELEASE_DATE));
            }

            return movieResults;
        }

        @Override
        protected Movie[] doInBackground(String... params) {
            //Build the URL string using the URIBuilder class
            Uri uriBuilder = Uri.parse(MOVIE_BASE_URL).buildUpon()
                             .appendQueryParameter(PARAM_SORT_BY, params[0])
                             .appendQueryParameter(PARAM_API_KEY, getString(R.string.value_api_key))
                             .build();


            //These two needs to be declared outside the try/catch block
            //so that they can be closed in the finally block
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            //Will contain the raw JSON string
            String movieJsonStr = null;

            String line = null;
            try {
                URL url = new URL(uriBuilder.toString());
                urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    return  null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                 movieJsonStr = buffer.toString();

               try {
                    return getMovieDataFromJson(movieJsonStr);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "Error trying to parse the JSON String");
                }

            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Error connecting to URL");
                return null;
            } catch (ProtocolException e) {
                Log.e(LOG_TAG, "Error with the URL protocoll");
                return null;
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error opening the URL Connection");
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error trying to close reader");
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Movie[] movieArray) {
            if (movieArray != null) {
                mMovieAdapter.clear();
                for(Movie movie: movieArray)
                    mMovieAdapter.add(movie);
            }
        }
    }
}
