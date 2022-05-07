package com.mobileapplication.mymovie_10.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mobileapplication.mymovie_10.R;
import com.mobileapplication.mymovie_10.db.AppSettingsEnity;
import com.mobileapplication.mymovie_10.db.MovieDAO;
import com.mobileapplication.mymovie_10.db.MyMovieDatabase;
import com.mobileapplication.mymovie_10.helpers.ApiService;
import com.mobileapplication.mymovie_10.helpers.CustomAdapter;
import com.mobileapplication.mymovie_10.helpers.Movie;

import org.json.JSONException;

import java.util.ArrayList;

public class SearchResultActivity extends AppCompatActivity {
    boolean fromMain;
    AppSettingsEnity appSettingsEnity;
    private RecyclerView recyclerView;
    private TextView no_data;
    private ImageView empty_imageview;
    private ArrayList<Movie> searchResults = new ArrayList<Movie>();
    private CustomAdapter customAdapter;
    private TextView result_overview;
    private FloatingActionButton back_to_search, button_sorting;
    private Spinner sortingSpinner;
    private String formerActivity = "/";
    private boolean ascending = true;
    private MyMovieDatabase database = null;
    private MovieDAO databaseDAO;

    // Hintergrund auf dem die Suchergebnisse angezeigt werden
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        initView();

        database = MyMovieDatabase.getSingletonInstance(this);
        databaseDAO = database.movieDao();

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(SearchResultActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.sortItem));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortingSpinner.setAdapter(myAdapter);

        // Übertragung der Ergebnis Daten
        Bundle extras = getIntent().getExtras();
        String[] jsonSearchResults = extras.getStringArray("movies");
        String originActivity = extras.getString("origin");
        formerActivity = extras.getString("last_activity");
        fromMain = formerActivity.equals("main");
        ApiService apiService = new ApiService();
        try {
            searchResults = apiService.parseJSON(jsonSearchResults);
            appSettingsEnity = databaseDAO.getSetting();
            if (appSettingsEnity.searchType == 1) {
                searchResults = trimSearchResults(appSettingsEnity.maxSearchHits);
            } else if (appSettingsEnity.searchType == 2) {
                searchResults = trimSearchResults(appSettingsEnity.maxPopularHits);
            } else if (appSettingsEnity.searchType == 3) {
                searchResults = trimSearchResults(appSettingsEnity.maxTopRatedHits);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Keine Suchergebnisse gefunden
        if (searchResults.size() == 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.no_movies_found_exception), Toast.LENGTH_LONG).show();
            empty_imageview.setVisibility(View.VISIBLE);
            no_data.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            // Initialisierung der Suchergebnisse wenn Ergebnisse vorhanden sind
            customAdapter = new CustomAdapter(SearchResultActivity.this, this, searchResults, jsonSearchResults, originActivity, fromMain);
            recyclerView.setAdapter(customAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(SearchResultActivity.this));
        }
        result_overview.setText(originActivity + " - " + searchResults.size() + " " + getString(R.string.results_counter));

        // Zurück zur Such Activity
        back_to_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (formerActivity.equals("main")) {
                    Intent intent = new Intent(SearchResultActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SearchResultActivity.this, SearchInputActivity.class);
                    startActivity(intent);
                }
            }
        });

        // Auswahl der Sortierungsrichtung
        button_sorting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = sortingSpinner.getSelectedItemPosition();
                if (ascending) {
                    ascending = false;
                    button_sorting.setImageResource(R.drawable.ic_sort_up);
                } else {
                    ascending = true;
                    button_sorting.setImageResource(R.drawable.ic_sort_down);
                }
                if (searchResults.size() > 0)
                    customAdapter = customAdapter.sortData(ascending, position);
                recyclerView.setAdapter(customAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(SearchResultActivity.this));
            }
        });

        // Auswahl des Sortierungsmerkmal
        sortingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (searchResults.size() > 0) {
                    customAdapter = customAdapter.sortData(ascending, adapterView.getSelectedItemPosition());
                    recyclerView.setAdapter(customAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(SearchResultActivity.this));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    // Initialisierung der Elemente der Activity
    private void initView() {
        no_data = findViewById(R.id.no_data);
        empty_imageview = findViewById(R.id.empty_imageview);
        recyclerView = findViewById(R.id.recyclerView);
        result_overview = findViewById(R.id.result_overview);
        back_to_search = findViewById(R.id.button_back_to_search);
        button_sorting = findViewById(R.id.button_filter_richtung_results);
        sortingSpinner = findViewById(R.id.spinner_filterauswahl_results);
    }
}