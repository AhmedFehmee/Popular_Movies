package com.example.ahmed.popularmovies;


import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.ahmed.popularmovies.data.DbOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainFragment extends Fragment {

    public final static String API_KEY = "b932ba435fc93a5944938fe9d44cd198";
    public final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?sort_by=&api_key=";

    public static int selectedMoviePos =0;
    public static int selectedFavPos =0;

    movieAdapter adapter;
    movieAdapter DbAdapter;
    GridView gridview;
    DbOpenHelper dbOpenHelper;
    Context context;
    String sortOrder;


    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sortOrder = getArguments().getString("SORT");
            if (sortOrder == "fav") {
                new FetchDataFromDb().execute();
            } else {
                new FetchDataFromApi().execute();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        gridview = (GridView) view.findViewById(R.id.gridView);
        context = getActivity();
        adapter = new movieAdapter(context, R.layout.movie_item, MainActivity.movies);
        return view;
    }

    public static MainFragment newInstance(String sortOrder) {
        Bundle args = new Bundle();
        args.putString("SORT", sortOrder);
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    class FetchDataFromApi extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            try {
                Uri uriBuilder = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter("sort_by", sortOrder)
                        .appendQueryParameter("api_key", API_KEY)
                        .build();
                URL url = new URL(uriBuilder.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                String response = sb.toString();
                ParseJsonData(response);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            gridview.setAdapter(adapter);
            if (MainActivity.isTablet()) {
                gridview.setItemChecked(selectedMoviePos, true);
                gridview.smoothScrollToPosition(selectedMoviePos);
                MainActivity.showDetails(selectedMoviePos);
            }
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long ss) {
                    MainActivity.showDetails(position);
                    selectedMoviePos = position;
                }
            });
        }
    }

    class FetchDataFromDb extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            dbOpenHelper = new DbOpenHelper(MainActivity.context);
            MainActivity.movies = dbOpenHelper.getAllFavoritePosters();
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            Log.v("database status", "Finished From Db");
            DbAdapter = new movieAdapter(context, R.layout.movie_item, MainActivity.movies);
            gridview.setAdapter(DbAdapter);
            if (MainActivity.isTablet()) {
                gridview.setItemChecked(selectedFavPos, true);
                gridview.smoothScrollToPosition(selectedMoviePos);
                MainActivity.showDetails(selectedFavPos);
            }
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long ss) {
                    MainActivity.showDetails(position);
                    selectedFavPos = position;
                }
            });
        }
    }

    private void ParseJsonData(String response) throws JSONException {
        String PosterBaseUrl = "http://image.tmdb.org/t/p/";
        String LargePoster = "w185";
        String SmallPoster = "";
        final String RESULTS = "results";
        final String ORIGINAL_TITLE = "original_title";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";
        final String POSTER_PATH = "poster_path";
        final String VOTE_AVERAGE = "vote_average";
        final String POPULARITY = "popularity";
        final String ID = "id";

        MainActivity.movies.clear();

        JSONObject jsono = new JSONObject(response);
        Log.v("Json", response);
        JSONArray jarray = jsono.getJSONArray(RESULTS);
        for (int i = 0; i < jarray.length(); i++) {
            JSONObject object = jarray.getJSONObject(i);
            String id = object.getString(ID);
            String title = object.getString(ORIGINAL_TITLE);
            String releaseDate = object.getString(RELEASE_DATE);
            String overView = object.getString(OVERVIEW);
            String voteAverage = object.getString(VOTE_AVERAGE);
            String popularity = object.getString(POPULARITY);
            String posterPath = object.getString(POSTER_PATH);
            String BigPoster = PosterBaseUrl + LargePoster + posterPath;
            String MinPoster = PosterBaseUrl + SmallPoster + posterPath;

            Movie m = new Movie();
            m.setId(id);
            m.setTitle(title);
            m.setReleaseDate(releaseDate);
            m.setOverView(overView);
            m.setVoteAverage(voteAverage);
            m.setPopularity(popularity);
            m.setLargePoster(BigPoster);
            m.setMinPoster(MinPoster);
            MainActivity.movies.add(m);
        }
    }

}








