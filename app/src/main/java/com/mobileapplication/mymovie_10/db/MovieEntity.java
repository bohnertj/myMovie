package com.mobileapplication.mymovie_10.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.mobileapplication.mymovie_10.helpers.Movie;

@Entity(tableName = "favourite_movies")
public class MovieEntity {

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "movie_id")
    @NonNull
    public int movieId;

    @ColumnInfo(name = "movie_name")
    public String movieName;

    @ColumnInfo(name = "movie_release_date")
    public String movieReleaseDate;

    @ColumnInfo(name = "movie_description")
    public String movieDescription;

    @ColumnInfo(name = "movie_image")
    public String movieImage;

    @ColumnInfo(name = "movie_age_rating")
    public boolean movieAgeRating;

    @ColumnInfo(name = "movie_voting")
    public double movieVoting;

    public MovieEntity(int movieId, String movieName, String movieReleaseDate, String movieDescription, String movieImage, boolean movieAgeRating, double movieVoting) {
        this.movieName = movieName;
        this.movieId = movieId;
        this.movieReleaseDate = movieReleaseDate;
        this.movieDescription = movieDescription;
        this.movieImage = movieImage;
        this.movieAgeRating = movieAgeRating;
        this.movieVoting = movieVoting;
    }
}
