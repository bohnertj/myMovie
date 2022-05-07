package com.mobileapplication.mymovie_10.helpers;

import com.mobileapplication.mymovie_10.db.MovieEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Movie implements Comparable<Movie> {
    private final String movie_title;
    private final String movie_releaseDate;
    private final String movie_description;
    private final String movie_img;
    private final double movie_vote;
    private final Boolean movie_ageRating;
    private Boolean movie_favourite;
    private int id;

    public Movie(int id, String movie_title, String movie_releaseDate, String movie_description, String movie_img, Boolean movie_ageRating, double movie_vote, boolean movie_favourite) {
        this.movie_title = movie_title;
        this.id = id;
        this.movie_releaseDate = movie_releaseDate;
        this.movie_description = movie_description;
        this.movie_img = movie_img;
        this.movie_ageRating = movie_ageRating;
        this.movie_vote = movie_vote;
        this.movie_favourite = movie_favourite;
    }

    public static ArrayList<Movie> parseInMovieList(List<MovieEntity> movieEntities) {
        ArrayList<Movie> bufferList = new ArrayList<Movie>();
        for (int i = 0; i < movieEntities.size(); i++) {
            bufferList.add(parseInMovieList(movieEntities.get(i)));
        }
        return bufferList;
    }

    private static Movie parseInMovieList(MovieEntity movieEntity) {
        return new Movie(movieEntity.movieId, movieEntity.movieName, movieEntity.movieReleaseDate, movieEntity.movieDescription, movieEntity.movieImage, movieEntity.movieAgeRating, movieEntity.movieVoting, true);
    }

    public String getMovie_title() {
        return movie_title;
    }

    public String getMovie_releaseDate() {
        return movie_releaseDate;
    }

    public String getMovie_description() {
        return movie_description;
    }

    public String getMovie_img() {
        return movie_img;
    }

    public double getMovie_vote() {
        return movie_vote;
    }

    public Boolean getMovie_ageRating() {
        return movie_ageRating;
    }

    public Boolean getMovie_favourite() {
        return movie_favourite;
    }

    public void setMovie_favourite(Boolean movie_favourite) {
        this.movie_favourite = movie_favourite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Movie)) return false;
        Movie movie = (Movie) o;
        return this.id == movie.id;
    }

    @Override
    public int compareTo(Movie movie) {
        return this.movie_title.compareTo(movie.movie_title);
    }

    public MovieEntity parseInMovieEntity() {
        return new MovieEntity(id, movie_title, movie_releaseDate, movie_description, movie_img, movie_ageRating, movie_vote);
    }

    public static class SortName implements Comparator<Movie> {
        @Override
        public int compare(Movie movie1, Movie movie2) {
            return movie1.movie_title.compareTo(movie2.movie_title);
        }
    }

    public static class SortRelease implements Comparator<Movie> {
        @Override
        public int compare(Movie movie1, Movie movie2) {
            int year1, year2;
            if (movie1.movie_releaseDate.substring(6, 10).equals("--")) year1 = 0;
            if (movie2.movie_releaseDate.substring(6, 10).equals("--")) year2 = 0;
            year1 = Integer.parseInt(movie1.movie_releaseDate.substring(6, 10));
            year2 = Integer.parseInt(movie2.movie_releaseDate.substring(6, 10));
            return year1 - year2;
        }
    }

    public static class SortVoting implements Comparator<Movie> {
        @Override
        public int compare(Movie movie1, Movie movie2) {
            return (int) (movie1.movie_vote * 10 - movie2.movie_vote * 10);
        }
    }
}