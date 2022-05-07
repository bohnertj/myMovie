package com.mobileapplication.mymovie_10.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mobileapplication.mymovie_10.R;
import com.mobileapplication.mymovie_10.helpers.Dialog;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FloatingActionButton button_search, button_top_rated, button_popular, button_settings, button_menu, button_sorting;
    private Spinner sortingSpinner;
    private ImageView empty_imageview;
    private TextView no_data, favourite_counter;
    private Context context;
    private boolean menuIsOpen = false;
    private boolean ascending = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        initView();
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.sortItem));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortingSpinner.setAdapter(myAdapter);

        // Wechselt auf die Such Activity
        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchInputActivity.class);
                startActivity(intent);
            }
        });

        // Wechselt zu den derzeit beliebten Filmen
        button_popular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkInternetConnection()) {
                    Dialog.showAlertDialog(context, getString(R.string.app_name), getString(R.string.main_activity_popular_exception));
                    return;
                }
            }
        });

        // Welcheslt zu am besten bewertet
        button_top_rated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkInternetConnection()) {
                    Dialog.showAlertDialog(context, getString(R.string.app_name), getString(R.string.main_activity_top_rated_exception));
                    return;
                }
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
            }
        });

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
                if (!menuIsOpen) {
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
    private void closeMenu() {
        menuIsOpen = false;
        button_settings.animate().translationYBy(getResources().getDimension(R.dimen.abs_1));
        button_top_rated.animate().translationYBy(getResources().getDimension(R.dimen.abs_2));
        button_popular.animate().translationYBy(getResources().getDimension(R.dimen.abs_3));
        button_search.animate().translationYBy(getResources().getDimension(R.dimen.abs_4));
    }

    // Pürft Internetverbindung
    private boolean checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }

    // Ruft API Request für die dezeit beliebten Filme auf
    protected class NowPopularMoviesThread extends Thread {
        @Override
        public void run() {
        }
    }
}

