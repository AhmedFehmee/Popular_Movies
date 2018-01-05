package com.example.ahmed.popularmovies;

/**
 * Created by ahmed on 11/22/2015.
 */
public class Movie {

    private String id;
    private String title;
    private String releaseDate;
    private String overView;
    private String voteAverage;
    private String popularity;
    private String MinPoster;
    private String LargePoster;

    public Movie() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getOverView() {
        return overView;
    }

    public void setOverView(String overView) {
        this.overView = overView;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public String getMinPoster() {
        return MinPoster;
    }

    public void setMinPoster(String minPoster) {
        MinPoster = minPoster;
    }

    public String getLargePoster() {
        return LargePoster;
    }

    public void setLargePoster(String largePoster) {
        LargePoster = largePoster;
    }

}
