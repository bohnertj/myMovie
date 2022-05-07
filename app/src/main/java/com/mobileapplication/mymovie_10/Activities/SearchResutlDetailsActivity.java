package com.mobileapplication.mymovie_10.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mobileapplication.mymovie_10.R;
import com.mobileapplication.mymovie_10.db.MovieDAO;
import com.mobileapplication.mymovie_10.db.MyMovieDatabase;
import com.mobileapplication.mymovie_10.helpers.ApiService;
import com.mobileapplication.mymovie_10.helpers.Movie;

public class SearchResutlDetailsActivity extends AppCompatActivity {
    private TextView movie_title, movie_release, movie_voting, movie_description, movie_favourite;
    private ImageView movie_cover;
    private FloatingActionButton favourite_button, back_to_search_result;
    private Movie movie;
    private final ApiService apiService = new ApiService();
    private String[] api_result;
    private String api_request;
    private String lastActivity = "";
    private boolean favourite;
    private MyMovieDatabase database = null;
    private MovieDAO movieDAO;

    // Zeigt Details zu einem ausgewähtem Film an
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_resutl_details);

        database = MyMovieDatabase.getSingletonInstance(this);
        movieDAO = database.movieDao();

        // Übergebene Daten werden aus Intent gezogen
        initView();
        Bundle extras = getIntent().getExtras();
        movie = new Movie(
                extras.getInt("movie_id"),
                extras.getString("movie_name"),
                extras.getString("movie_release"),
                extras.getString("movie_description"),
                extras.getString("movie_cover"),
                extras.getBoolean("movie_age"),
                extras.getDouble("movie_score"),
                extras.getBoolean("movie_isFavourite"));

        api_result = extras.getStringArray("api_result");
        api_request = extras.getString("api_search_text");
        lastActivity = extras.getString("last_activity");
        setFavouriteButton(movie.getMovie_favourite());

        // Film Cover laden
        apiService.setMovieImg(movie.getMovie_img(), getApplicationContext(), movie_cover);
        movie_title.setText(movie.getMovie_title());
        movie_release.setText(getString(R.string.results_details_release) + " " + movie.getMovie_releaseDate());
        movie_voting.setText(movie.getMovie_vote() + "/10.0");
        movie_description.setText(getString(R.string.results_details_description) + movie.getMovie_description());

        // Film als Favorit makieren/nicht mehr makieren
        favourite_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (favourite == true) {
                    // aus favouriten löschen
                    movieDAO.deleteMovie(movie.getId());
                    setFavouriteButton(false);
                } else {
                    // favouriten  in DB hinzufügen
                    movieDAO.insertMovie(movie.parseInMovieEntity());
                    setFavouriteButton(true);
                }
            }
        });

        // Zurück zu Suchergebnis Activity
        back_to_search_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (api_result != null) {
                    Intent intent = new Intent(SearchResutlDetailsActivity.this, SearchResultActivity.class);
                    intent.putExtra("movies", api_result);
                    intent.putExtra("origin", api_request);
                    if (lastActivity.equals("main")) {
                        intent.putExtra("last_activity", "main");
                    } else {
                        intent.putExtra("last_activity", "search");
                    }
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SearchResutlDetailsActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    // Änderung des Favoriten Icon je nach auswahl
    private void setFavouriteButton(boolean isFavourite) {
        if (isFavourite == true) {
            favourite_button.setImageResource(R.drawable.heart_filled_icon);
            movie_favourite.setVisibility(View.VISIBLE);
        } else {
            favourite_button.setImageResource(R.drawable.heart_not_filled_icon);
            movie_favourite.setVisibility(View.INVISIBLE);
        }
        favourite = isFavourite;
    }

    //Initialisierung der Elemente der Acitvity
    private void initView() {
        movie_title = findViewById(R.id.movie_title);
        movie_release = findViewById(R.id.movie_release);
        movie_voting = findViewById(R.id.movie_voting);
        movie_cover = findViewById(R.id.movie_cover);
        movie_favourite = findViewById(R.id.movieDetailsFavourite);
        movie_description = findViewById(R.id.movie_description);
        movie_description.setMovementMethod(new ScrollingMovementMethod());
        favourite_button = findViewById(R.id.movie_favourite_button);
        back_to_search_result = findViewById(R.id.button_back_to_search_result);
    }
}