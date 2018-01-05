package com.example.ahmed.popularmovies.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.example.ahmed.popularmovies.Movie;

import java.util.ArrayList;

/**
 * Created by ahmed on 12/17/2015.
 */
public class DbOpenHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    public static final String DATABASE_NAME = "fav.db";
    // Table name
    public static final String TABLE_NAME = "favourites";

    public static abstract class FeedEntry implements BaseColumns {
        public static final String KEY_ID = "id";
        public static final String KEY_LARGE_POSTER_URL = "largePoster";
        public static final String KEY_MINI_POSTER_URL = "minPoster";
        public static final String KEY_MOVIE_NAME = "movieName";
        public static final String KEY_RELEASE_DATE = "releaseDate";
        public static final String KEY_VOTE_AVERAGE = "vote_average";
        public static final String KEY_OVERVIEW = "overview";
    }

    public DbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FAVORITES_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + FeedEntry.KEY_ID + " TEXT,"
                + FeedEntry.KEY_LARGE_POSTER_URL + " TEXT,"
                + FeedEntry.KEY_MINI_POSTER_URL + " TEXT, "
                + FeedEntry.KEY_MOVIE_NAME + " TEXT,"
                + FeedEntry.KEY_RELEASE_DATE + " TEXT,"
                + FeedEntry.KEY_OVERVIEW + " TEXT,"
                + FeedEntry.KEY_VOTE_AVERAGE + " TEXT"
                + ")";
        db.execSQL(CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void Delete (){
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(TABLE_NAME,null,null);
    }

    public ArrayList<Movie> getAllFavoritePosters() {

        String SQL_SELECT = "SELECT * FROM " + TABLE_NAME;
        ArrayList<Movie> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL_SELECT, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            String Id = cursor.getString(cursor.getColumnIndex(FeedEntry.KEY_ID));
            String PosterUrl = cursor.getString(cursor.getColumnIndex(FeedEntry.KEY_LARGE_POSTER_URL));
            String MinPosterUrl = cursor.getString(cursor.getColumnIndex(FeedEntry.KEY_MINI_POSTER_URL));
            String Title = cursor.getString(cursor.getColumnIndex(FeedEntry.KEY_MOVIE_NAME));
            String ReleaseDate = cursor.getString(cursor.getColumnIndex(FeedEntry.KEY_RELEASE_DATE));
            String VoteAverage = cursor.getString(cursor.getColumnIndex(FeedEntry.KEY_VOTE_AVERAGE));
            String Overview = cursor.getString(cursor.getColumnIndex(FeedEntry.KEY_OVERVIEW));

            Movie m = new Movie();
            m.setId(Id);
            m.setLargePoster(PosterUrl);
            m.setMinPoster(MinPosterUrl);
            m.setTitle(Title);
            m.setReleaseDate(ReleaseDate);
            m.setVoteAverage(VoteAverage);
            m.setOverView(Overview);
            list.add(m);
            Log.v("database status", "Object is added to db");
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return list;
    }

}
