package com.wernerraubenheimer.popularmovies;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Werner on 2015/08/11.
 */
public class Movie implements Parcelable {

    private String originalTitle;
    private String posterPath;
    private String overView;
    private String voteAverage;
    private String releaseDate;

    public Movie() {

    }

    public Movie (String originalTitle, String posterPath, String overView, String voteAverage, String releaseDate) {
        setOriginalTitle(originalTitle);
        setPosterPath(posterPath);
        setOverView(overView);
        setReleaseDate(releaseDate);
        setVoteAverage(voteAverage);
    }

    public Movie(Parcel in) {
        originalTitle = in.readString();
        posterPath = in.readString();
        overView = in.readString();
        voteAverage = in.readString();
        releaseDate = in.readString();
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
        Uri uriBuilder = Uri.parse("http://image.tmdb.org/t/p/w185/").buildUpon()
                         .appendPath(posterPath)
                          .build();
        //this.posterPath = uriBuilder.toString();
        //this.posterPath = "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg";
        this.posterPath = "http://image.tmdb.org/t/p/w185/" + posterPath;
    }

    public String getOverView() {
        return overView;
    }

    public void setOverView(String overView) {
        if (overView != null) {
            this.overView = overView;
        } else {
            this.overView = "No overview available";
        }
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String toString() {
        return " --- " + getOriginalTitle() + " --- " + getReleaseDate();
    }
    /*
     * Implementing the parceble methods as per the webcast
     */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(getOriginalTitle());
        parcel.writeString(getPosterPath());
        parcel.writeString(getOverView());
        parcel.writeString(getVoteAverage());
        parcel.writeString(getReleaseDate());
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}