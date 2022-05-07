package com.mobileapplication.mymovie_10.db;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class MovieDAO {

    @Insert(onConflict = REPLACE)
    public abstract void insertMovie(MovieEntity movieEntity);

    @Query("DELETE FROM favourite_movies")
    public abstract void deleteAllMovies();

    @Query("SELECT * FROM favourite_movies")
    public abstract List<MovieEntity> getAllMovies();

    @Query("DELETE FROM favourite_movies WHERE movie_id = :id")
    public abstract void deleteMovie(int id);

    @Query("SELECT * FROM favourite_movies WHERE movie_id = :id")
    public abstract MovieEntity getMovie(int id);

    @Query("SELECT movie_id FROM favourite_movies WHERE movie_id = :movieID ")
    public abstract int getEntityId(int movieID);

    @Query("UPDATE favourite_movies SET movie_name = :movieName AND movie_id WHERE movie_id = :movieId")
    public abstract void updateMovieTitel(int movieId, String movieName);

    @Query("UPDATE favourite_movies SET movie_description = :movieDescription AND movie_id WHERE movie_id = :movieId")
    public abstract void updateMovieDescription(int movieId, String movieDescription);

    public void updateMovie(int movieId, MovieEntity newMovie) {
        deleteMovie(movieId);
        insertMovie(newMovie);
    }

    public void updateFavourites(ArrayList<MovieEntity> list) {
        for(int i = 0; i < list.size(); i++) {
            updateMovie(list.get(i).movieId, list.get(i));
        }
    }

    // Settings
    @Insert(onConflict = REPLACE)
    public abstract void insertSettings(AppSettingsEnity appSettingsEnity);

    @Query("DELETE FROM app_settings")
    public abstract void deleteAllSettings();

    @Query("Select * FROM app_settings")
    public abstract AppSettingsEnity getSetting();

    public void updateSetting(AppSettingsEnity appSettingsEnity) {
        deleteAllSettings();
        insertSettings(appSettingsEnity);
    }

    @Query("SELECT COUNT(*) FROM app_settings")
    public abstract int getAmountSetting();
}
