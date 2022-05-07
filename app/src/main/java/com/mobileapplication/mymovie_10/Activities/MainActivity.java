package com.mobileapplication.mymovie_10.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mobileapplication.mymovie_10.R;
import com.mobileapplication.mymovie_10.db.AppSettingsEnity;
import com.mobileapplication.mymovie_10.db.MyMovieDatabase;
import com.mobileapplication.mymovie_10.db.MovieDAO;
import com.mobileapplication.mymovie_10.helpers.ApiService;
import com.mobileapplication.mymovie_10.helpers.CustomAdapter;
import com.mobileapplication.mymovie_10.helpers.Dialog;
import com.mobileapplication.mymovie_10.helpers.Movie;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FloatingActionButton button_search, button_top_rated, button_popular, button_settings, button_menu, button_sorting;
    private Spinner sortingSpinner;
    private ImageView empty_imageview;
    private TextView no_data, favourite_counter;
    private ApiService apiService = new ApiService();
    private ArrayList<Movie> favouriteList = new ArrayList<Movie>();
    private CustomAdapter customAdapter;
    private MyMovieDatabase database = null;
    private MovieDAO databaseDAO;
    private Context context;
    private boolean menuIsOpen = false;
    private boolean ascending = true;
    private AppSettingsEnity appSettingsEnity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        database = MyMovieDatabase.getSingletonInstance(this);
        databaseDAO = database.movieDao();

        // initialisiert Settings mit der Standard Einstellung beim ersten Start der App
        if(databaseDAO.getAmountSetting()==0) {
            databaseDAO.insertSettings(new AppSettingsEnity("de", "DE", 150, 100,100, 0));
        }

        // lädt Favoriten Liste aus der Datenbank
        favouriteList = Movie.parseInMovieList(databaseDAO.getAllMovies());
        appSettingsEnity = databaseDAO.getSetting();

        initView();
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.sortItem));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortingSpinner.setAdapter(myAdapter);

        // Wechselt auf die Such Activity
        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appSettingsEnity.searchType = 1;
                databaseDAO.updateSetting(appSettingsEnity);
                Intent intent = new Intent(MainActivity.this, SearchInputActivity.class);
                startActivity(intent);
            }
        });

        // Wechselt zu den derzeit beliebten Filmen
        button_popular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkInternetConnection()){
                    Dialog.showAlertDialog(context, getString(R.string.app_name), getString(R.string.main_activity_popular_exception));
                    return;
                }
                appSettingsEnity.searchType = 2;
                databaseDAO.updateSetting(appSettingsEnity);
            }
        });

        // Welcheslt zu am besten bewertet
        button_top_rated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkInternetConnection()){
                    Dialog.showAlertDialog(context, getString(R.string.app_name), getString(R.string.main_activity_top_rated_exception));
                    return;
                }
                appSettingsEnity.searchType = 3;
                databaseDAO.updateSetting(appSettingsEnity);
                TopRatedMoviesThread latestMoviesThread = new TopRatedMoviesThread();
                latestMoviesThread.start();
            }
        });

        // Wechsel zu den Einstellungen
        button_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        // Befüllung der Favoriten Liste
        if(favouriteList.size()==0){
            empty_imageview.setVisibility(View.VISIBLE);
            no_data.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            customAdapter = new CustomAdapter(MainActivity.this, this, favouriteList, null, null, true);
            recyclerView.setAdapter(customAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        }

        // Wendet die aktuelle Sortierung auf- bzw. absteigend an
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
                if(favouriteList.size()>0) customAdapter = customAdapter.sortData(ascending, position);
                recyclerView.setAdapter(customAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            }
        });

        // Legt das Merkmal fest nach dem sortiert werden soll
        sortingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(favouriteList.size()>0) {
                    customAdapter = customAdapter.sortData(ascending, adapterView.getSelectedItemPosition());
                    recyclerView.setAdapter(customAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        // Zeigt die Anzahl der Filme in der Favoriten Liste an
        favourite_counter.setText(getString(R.string.main_activity_score_title) + " " + favouriteList.size() + " " + getString(R.string.main_activity_score_counter));
    }

    // initialisiert Elmente der Activity
    private void initView() {
        no_data = findViewById(R.id.no_data);
        empty_imageview = findViewById(R.id.empty_imageview);
        recyclerView = findViewById(R.id.recyclerView);
        button_search = findViewById(R.id.button_search);
        button_top_rated = findViewById(R.id.button_top_rated);
        button_popular = findViewById(R.id.button_now_popular);
        favourite_counter = findViewById(R.id.result_overview_favourite);
        button_settings = findViewById(R.id.button_settings);
        button_menu = findViewById(R.id.menu_button);
        button_sorting = findViewById(R.id.button_filter_richtung);
        sortingSpinner = findViewById(R.id.spinner_filterauswahl);

        button_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!menuIsOpen) {
                    openMenu();
                } else {
                    closeMenu();
                }
            }
        });
    }

    // Animiert Öffnung des Menüs
    private void openMenu() {
        menuIsOpen = true;
        button_settings.animate().translationYBy(-getResources().getDimension(R.dimen.abs_1));
        button_top_rated.animate().translationYBy(-getResources().getDimension(R.dimen.abs_2));
        button_popular.animate().translationYBy(-getResources().getDimension(R.dimen.abs_3));
        button_search.animate().translationYBy(-getResources().getDimension(R.dimen.abs_4));
    }

    // Animiert Schließung des Menüs
    private void closeMenu(){
        menuIsOpen = false;
        button_settings.animate().translationYBy(getResources().getDimension(R.dimen.abs_1));
        button_top_rated.animate().translationYBy(getResources().getDimension(R.dimen.abs_2));
        button_popular.animate().translationYBy(getResources().getDimension(R.dimen.abs_3));
        button_search.animate().translationYBy(getResources().getDimension(R.dimen.abs_4));
    }

    // Ruft API Request für die am besten bewertetsten Filme auf
    protected class TopRatedMoviesThread extends Thread {
        @Override
        public void run() {
            try {
                Intent intent = new Intent(MainActivity.this, SearchResultActivity.class);
                String language = appSettingsEnity.settingAppLanguage;
                intent.putExtra("movies", apiService.getTopRatedMovies(language, appSettingsEnity.settingRegion, appSettingsEnity.maxTopRatedHits));
                intent.putExtra("origin", getString(R.string.top_rated_score_title));
                intent.putExtra("last_activity", "main");
                startActivity(intent);
            } catch (Exception e) {
                Log.e("MyMovie","Exception",e);

            }
        }
    }

    // Pürft Internetverbindung
    private boolean checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }
        else
            return false;
    }
}