package com.example.ahmed.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static final String popluar = "popularity.desc";
    public static final String top_rate = "vote_average.desc";
    public static Context context;
    public static ArrayList<Movie> movies = new ArrayList<>();
    public static FragmentManager fm;
    public static Configuration config;
    public static DetailFragment detailFragment;
    public static List<Fragment> fragmentList = null;

    public static String id;
    public static String title;
    public static String releaseDate;
    public static String overView;
    public static String voteAverage;
    public static String popularity;
    public static String poster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = MainActivity.this;
        fm = getSupportFragmentManager();
        config = getResources().getConfiguration();
        ReplaceFragments(popluar);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.most_popular) {
            ReplaceFragments(popluar);
            return true;
        }
        if (id == R.id.highest_rated) {
            ReplaceFragments(top_rate);
            return true;
        }
        if (id == R.id.favorite) {
            ReplaceFragments("fav");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static void ReplaceFragments(String SortBy) {
        if (isTablet()) {
            RemoveOldDetailFragment();
            MainFragment mainFragment = MainFragment.newInstance(SortBy);
            fm.beginTransaction().replace(R.id.tabletMain, mainFragment)
                    .commit();
        } else {
            MainFragment mainFragment = MainFragment.newInstance(SortBy);
            fm.beginTransaction().replace(R.id.fragmentMain, mainFragment)
                    .commit();
        }
    }

    private static void RemoveOldDetailFragment() {
        if (fragmentList != null) {
            for (int i = 0; i <= fragmentList.size() - 1; i++) {
                Log.v("debug", String.valueOf(fragmentList.size()));
                try {
                    if (fragmentList.get(i).getTag() == ("tag")) {
                        fm.beginTransaction().remove(fragmentList.get(i)).commit();
                    }
                } catch (NullPointerException e) {
                }

            }
        }
    }

    public static void showDetails(int position) {
        if (movies.isEmpty()) {
            if (isTablet()) {
                RemoveOldDetailFragment();
            }
        } else {
            id = movies.get(position).getId();
            title = movies.get(position).getTitle();
            releaseDate = movies.get(position).getReleaseDate();
            overView = movies.get(position).getOverView();
            voteAverage = movies.get(position).getVoteAverage();
            popularity = movies.get(position).getPopularity();
            poster = movies.get(position).getLargePoster();
            if (isTablet()) {
                detailFragment = new DetailFragment();
                fm.beginTransaction().replace(R.id.tabletDetail, detailFragment, "tag")
                        .commit();
                fragmentList = fm.getFragments();
            } else {
                Intent intent = new Intent(context, DetailedActivity.class);
                context.startActivity(intent);
            }
        }
    }

    public static boolean isTablet() {
        if (config.smallestScreenWidthDp >= 600) {
            return true;
        } else {
            return false;
        }
    }

}





