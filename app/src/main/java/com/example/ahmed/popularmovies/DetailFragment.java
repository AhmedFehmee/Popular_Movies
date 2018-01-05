package com.example.ahmed.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahmed.popularmovies.data.DbOpenHelper;
import com.squareup.picasso.Picasso;

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
import java.util.ArrayList;

/**
 * Created by ahmed on 12/29/2015.
 */
public class DetailFragment extends Fragment {

    private View view;
    private TextView MovieName;
    private TextView ReleaseDate;
    private TextView OverView;
    private TextView VoteAverage;
    private ImageView MiniPoster;
    private Button Fav;
    private TextView tvFirstReviewAuthor;
    private TextView tvFirstReview;
    private TextView tvSecondReviewAuthor;
    private TextView tvSecondReview;
    private TextView tvFirstTrailerName;
    private TextView tvSecondTrailerName;
    private ImageView ivPlayFirstTrailer;
    private ImageView ivPlaySecondTrailer;

    public static Context context;

    public DetailFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context=DetailedActivity.context;
        if (MainActivity.isTablet()){
            context = MainActivity.context;
        }
        Fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddToFavourites();
            }
        });
        if (CheckDbRedundancy()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Fav.setBackground(getActivity().getDrawable(R.drawable.favourite));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Fav.setBackground(getActivity().getDrawable(R.drawable.blank));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_detail, container, false);
        findViews();
        AssignDataToViews();
        new FetchReviewsFromApi().execute();
        new FetchMovieTrailers().execute();
        return view;
    }

    private void findViews() {
        MovieName = (TextView) view.findViewById(R.id.tvMovieName);
        ReleaseDate = (TextView) view.findViewById(R.id.tvReleaseDate);
        OverView = (TextView) view.findViewById(R.id.tvOverview);
        VoteAverage = (TextView) view.findViewById(R.id.tvVoteAverage);
        MiniPoster = (ImageView) view.findViewById(R.id.ivMiniPoster);
        Fav = (Button) view.findViewById(R.id.ivMakeItFavorite);
        tvFirstReviewAuthor = (TextView) view.findViewById(R.id.tvFirstAuthor);
        tvFirstReview = (TextView) view.findViewById(R.id.tvFirstReview);
        tvSecondReviewAuthor = (TextView) view.findViewById(R.id.tvSecondAuthor);
        tvSecondReview = (TextView) view.findViewById(R.id.tvSecondReview);
        tvFirstTrailerName = (TextView) view.findViewById(R.id.tvFirstTrailer);
        tvSecondTrailerName = (TextView) view.findViewById(R.id.tvSecondTrailer);
        ivPlayFirstTrailer = (ImageView) view.findViewById(R.id.ivPlayTrailer1);
        ivPlaySecondTrailer = (ImageView) view.findViewById(R.id.ivPlayTrailer2);
    }

    private void AssignDataToViews() {
        MovieName.setText(MainActivity.title);
        OverView.setText(MainActivity.overView);
        ReleaseDate.setText(MainActivity.releaseDate);
        VoteAverage.setText(MainActivity.voteAverage+"/10");
        Picasso.with(context).load(MainActivity.poster).into(MiniPoster);
    }

    private class FetchReviewsFromApi extends AsyncTask<String, Void, Boolean> {
        String movieId = MainActivity.id;
        final String BASE_URL = "http://api.themoviedb.org/3/movie/" + movieId + "/reviews?api_key=";
        final String RESULTS = "results";
        final String CONTENT = "content";
        final String AUTHOR = "author";

        String firstAuthor;
        String firstContent;
        String secondAuthor;
        String secondContent;

        @Override
        protected Boolean doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            try {
                Uri uriBuilder = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter("api_key", MainFragment.API_KEY)
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
                JSONObject jsonObject = new JSONObject(response);
                JSONArray resultsArray = jsonObject.getJSONArray(RESULTS);
                JSONObject firstObject = resultsArray.getJSONObject(0);
                String firstAuthorName = firstObject.getString(AUTHOR);
                firstAuthor = firstAuthorName;
                firstContent = firstObject.getString(CONTENT);
                JSONObject secondObject = resultsArray.getJSONObject(1);
                secondAuthor = secondObject.getString(AUTHOR);
                secondContent = secondObject.getString(CONTENT);

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
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            tvFirstReviewAuthor.setText(firstAuthor);
            tvFirstReviewAuthor.setTypeface(Typeface.MONOSPACE);
            tvFirstReview.setText(firstContent);
            tvFirstReview.setTypeface(Typeface.SANS_SERIF);
            tvSecondReviewAuthor.setText(secondAuthor);
            tvSecondReviewAuthor.setTypeface(Typeface.MONOSPACE);
            tvSecondReview.setText(secondContent);
            tvSecondReview.setTypeface(Typeface.SANS_SERIF);
            Log.v("debug", "3");
        }
    }

    public class FetchMovieTrailers extends AsyncTask<Void, Void, String> {
        String movieId = MainActivity.id;
        String firstTrailerName;
        String firstSource;
        String secondTrailerName;
        String secondSource;

        final String BASE_URL = "http://api.themoviedb.org/3/movie/" + movieId + "/trailers?api_key=";
        final String YOUTUBE = "youtube";
        final String NAME = "name";
        final String SOURCE = "source";

        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            try {
                Uri uriBuilder = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter("api_key", MainFragment.API_KEY)
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
                JSONObject jsonObject = new JSONObject(response);
                JSONArray resultsArray = jsonObject.getJSONArray(YOUTUBE);
                JSONObject firstObject = resultsArray.getJSONObject(0);
                firstTrailerName = firstObject.getString(NAME);
                firstSource = firstObject.getString(SOURCE);
                if (resultsArray.length() >= 2) {
                    JSONObject secondObject = resultsArray.getJSONObject(1);
                    secondTrailerName = secondObject.getString(NAME);
                    secondSource = secondObject.getString(SOURCE);
                }

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
        protected void onPostExecute(String string) {
            tvFirstTrailerName.setText(firstTrailerName);
            tvFirstTrailerName.setTypeface(Typeface.SANS_SERIF);
            tvSecondTrailerName.setText(secondTrailerName);
            tvSecondTrailerName.setTypeface(Typeface.SANS_SERIF);
            ivPlayFirstTrailer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com" +
                            "/watch?v=" + firstSource)));
                }
            });
            ivPlaySecondTrailer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com" +
                            "/watch?v=" + secondSource)));
                }
            });
        }
    }

    private void AddToFavourites() {
        DbOpenHelper dbHelper = new DbOpenHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (CheckDbRedundancy()) {
            Toast.makeText(context, "This movie is already in Favourites", Toast.LENGTH_SHORT).show();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Fav.setBackground(getActivity().getDrawable(R.drawable.favourite));
            }
            values.put(DbOpenHelper.FeedEntry.KEY_ID, MainActivity.id);
            values.put(DbOpenHelper.FeedEntry.KEY_LARGE_POSTER_URL, MainActivity.poster);
            values.put(DbOpenHelper.FeedEntry.KEY_MOVIE_NAME, MainActivity.title);
            values.put(DbOpenHelper.FeedEntry.KEY_RELEASE_DATE, MainActivity.releaseDate);
            values.put(DbOpenHelper.FeedEntry.KEY_VOTE_AVERAGE, MainActivity.voteAverage);
            values.put(DbOpenHelper.FeedEntry.KEY_OVERVIEW, MainActivity.overView);
            db.insert(DbOpenHelper.TABLE_NAME, null, values);
            Toast.makeText(context, "Movie has been added to Favourites", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    private boolean CheckDbRedundancy() {
        DbOpenHelper dbHelper = new DbOpenHelper(context);
        ArrayList<Movie> list = new ArrayList<>();
        String Oldtitle;
        boolean b = false;
        list = dbHelper.getAllFavoritePosters();
        for (int i = 0; i < list.size(); i++) {
            Oldtitle = list.get(i).getTitle();
            if (Oldtitle.equals(MainActivity.title)) {
                b = true;
            }
        }
        return b;
    }

}


