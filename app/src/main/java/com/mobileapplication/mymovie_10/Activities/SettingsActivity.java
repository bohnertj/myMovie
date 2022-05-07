package com.mobileapplication.mymovie_10.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mobileapplication.mymovie_10.R;
import com.mobileapplication.mymovie_10.db.AppSettingsEnity;
import com.mobileapplication.mymovie_10.db.MovieDAO;
import com.mobileapplication.mymovie_10.db.MovieEntity;
import com.mobileapplication.mymovie_10.db.MyMovieDatabase;
import com.mobileapplication.mymovie_10.helpers.ApiService;
import com.mobileapplication.mymovie_10.helpers.Dialog;
import com.mobileapplication.mymovie_10.helpers.Movie;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {
    private EditText hits_search, hits_popular, hit_top_rated;
    private Spinner language;
    private FloatingActionButton button_confirm, back_to_main;
    private MyMovieDatabase database = null;
    private MovieDAO databaseDAO;
    private AppSettingsEnity appSettingsEnity;
    private ArrayList<Movie> favourits;
    private Context context;

    // Setzen der Einstellungen
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initView();
        context = this;
        database = MyMovieDatabase.getSingletonInstance(this);
        databaseDAO = database.movieDao();
        // Ruft aktuelle Einstellungen aus Datenbank ab
        appSettingsEnity = databaseDAO.getSetting();

        // Befüllt Einstellungs Textfelder mit Daten der aktuellen Einstellung
        hits_search.setHint("" + appSettingsEnity.maxSearchHits);
        hits_popular.setHint("" + appSettingsEnity.maxPopularHits);
        hit_top_rated.setHint("" + appSettingsEnity.maxTopRatedHits);

        appSettingsEnity = databaseDAO.getSetting();
        favourits = Movie.parseInMovieList(databaseDAO.getAllMovies());

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(SettingsActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.languages));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        language.setAdapter(myAdapter);

        if (appSettingsEnity.settingAppLanguage.equals("de")) {
            selectValue(language, getString(R.string.la_de));
        } else {
            selectValue(language, getString(R.string.la_en));
        }

        // Springt zurück zur Main Activity
        back_to_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Übernimmt Einstellungen und aktuallisiert die entsprechenden Daten
        button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int maxSearchHits, maxPopularHits, maxTopRatedHits;
                String lang = language.getSelectedItem().toString(), region;

                // Änderung der Sprache
                if (lang.equals("deutsch")) {
                    lang = "de";
                    region = "DE";
                } else {
                    lang = "en-US";
                    region = "US";
                }

                // Auslesen der Textfelder
                try {
                    maxSearchHits = Integer.parseInt(hits_search.getText().toString());
                } catch (Exception e) {
                    maxSearchHits = appSettingsEnity.maxSearchHits;
                }
                try {
                    maxPopularHits = Integer.parseInt(hits_popular.getText().toString());
                } catch (Exception e) {
                    maxPopularHits = appSettingsEnity.maxPopularHits;
                }
                try {
                    maxTopRatedHits = Integer.parseInt(hit_top_rated.getText().toString());
                } catch (Exception e) {
                    maxTopRatedHits = appSettingsEnity.maxTopRatedHits;
                }

                // Exceptionhanling der Eingegebenen Parameter
                if (maxSearchHits > 300 || maxSearchHits < 20) {
                    Toast.makeText(getApplicationContext(), getString(R.string.settings_max_search_hits_exception), Toast.LENGTH_SHORT).show();
                } else if (maxPopularHits > 300 || maxPopularHits < 20) {
                    Toast.makeText(getApplicationContext(), getString(R.string.settings_max_popular_hits_exception), Toast.LENGTH_SHORT).show();
                } else if (maxTopRatedHits > 300 || maxTopRatedHits < 20) {
                    Toast.makeText(getApplicationContext(), getString(R.string.settings_max_top_rated_hits_exception), Toast.LENGTH_SHORT).show();
                } else {
                    appSettingsEnity.searchType = 0;
                    boolean equalLanguage = lang.equals(appSettingsEnity.settingAppLanguage);
                    appSettingsEnity.maxSearchHits = maxSearchHits;
                    appSettingsEnity.maxPopularHits = maxPopularHits;
                    appSettingsEnity.maxTopRatedHits = maxTopRatedHits;
                    appSettingsEnity.settingAppLanguage = lang;
                    appSettingsEnity.settingRegion = region;
                    databaseDAO.updateSetting(appSettingsEnity);

                    if (!equalLanguage) {
                        if (!checkInternetConnection()) {
                            Dialog.showAlertDialog(context, getString(R.string.app_name), getString(R.string.settings_no_connection_exception));
                        } else {
                            UpdateFavouritsThread updateFavouritsThread = new UpdateFavouritsThread();
                            updateFavouritsThread.start();
                        }
                    } else {
                        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    // Initialisiert Elemente der Acitivity
    private void initView() {
        hits_search = findViewById(R.id.max_hits_search);
        hits_popular = findViewById(R.id.max_hits_popular);
        hit_top_rated = findViewById(R.id.max_top_rated);
        language = findViewById(R.id.language_selection);
        button_confirm = findViewById(R.id.confirm_settings);
        back_to_main = findViewById(R.id.button_settings_back_main);
    }

    private void selectValue(Spinner spinner, Object value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    // Prüft Internetverbindung
    private boolean checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }

    // Führt API Request bei Veränderung der (Beschreibungs) Sprache, um neue Beschreibung zu setzten
    protected class UpdateFavouritsThread extends Thread {
        @Override
        public void run() {
            try {
                ApiService apiService = new ApiService();
                String language = appSettingsEnity.settingAppLanguage;

                ArrayList<MovieEntity> pufferList = new ArrayList<MovieEntity>();
                Movie pufferMovie;

                for (int i = 0; i < favourits.size(); i++) {
                    pufferMovie = apiService.pareJSON(apiService.getFindMovieById(favourits.get(i).getId(), language));
                    System.out.println(pufferMovie.getMovie_title());
                    pufferList.add(pufferMovie.parseInMovieEntity());
                }
                databaseDAO.updateFavourites(pufferList);
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                Log.e("MyMovie", "Exception", e);
            }
        }
    }
}