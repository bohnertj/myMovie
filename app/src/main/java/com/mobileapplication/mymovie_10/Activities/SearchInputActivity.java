package com.mobileapplication.mymovie_10.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mobileapplication.mymovie_10.R;
import com.mobileapplication.mymovie_10.db.AppSettingsEnity;
import com.mobileapplication.mymovie_10.db.MovieDAO;
import com.mobileapplication.mymovie_10.db.MyMovieDatabase;
import com.mobileapplication.mymovie_10.helpers.ApiService;
import com.mobileapplication.mymovie_10.helpers.Dialog;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SearchInputActivity extends AppCompatActivity {

    private static String movie_title;
    private static int movie_year;
    private static Boolean movie_fsk18;
    private EditText input_name, input_year;
    private Switch input_fsk18;
    private FloatingActionButton button_confirm, button_back;
    private Context context;
    private MyMovieDatabase database = null;
    private MovieDAO databaseDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        context = this;

        // Lädt aktuelle Einstellung aus der Datenbank
        database = MyMovieDatabase.getSingletonInstance(this);
        databaseDAO = database.movieDao();

        // Führt Suche aus
        button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkInternetConnection()) {
                    Dialog.showAlertDialog(context, getString(R.string.app_name), getString(R.string.search_input_activity_exception));
                } else {
                    try {
                        onButtonSearch(view);
                    } catch (IOException e) {
                        Log.e(getString(R.string.app_name), getString(R.string.exception), e);
                    }
                }
            }
        });

        // Springt zurück auf die Main Activity
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchInputActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    // Methode die bei klick auf den Suche Button ausgeführt wird
    public void onButtonSearch(View view) throws IOException {
        // Kein eintrag Exception
        movie_title = input_name.getText().toString();
        if (movie_title.equals("")) {
            Toast.makeText(getApplicationContext(), getString(R.string.search_input_activity_no_search_input_exception), Toast.LENGTH_SHORT).show();
            return;
        }
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        int currentYear = Integer.parseInt(dateFormat.format(calendar.getTime()));
        if (!input_year.getText().toString().equals("")) {
            movie_year = Integer.parseInt(input_year.getText().toString());
            if (movie_year < 1900 || movie_year > currentYear) {
                Toast.makeText(getApplicationContext(), getString(R.string.search_input_time_exception_1) + " " + currentYear + " " + getString(R.string.search_input_time_exception_2), Toast.LENGTH_LONG).show();
                return;
            }
        } else {
            movie_year = 0;
        }
        movie_fsk18 = input_fsk18.isChecked();
        MyThread mt = new MyThread();
        mt.start();
    }

    // Initialisierung der Elemente der Activity
    private void initView() {
        button_confirm = findViewById(R.id.confirm_button);
        button_back = findViewById(R.id.button_back_to_fvourits);
        input_name = findViewById(R.id.editTextName);
        input_year = findViewById(R.id.editTextYear);
        input_fsk18 = findViewById(R.id.switchFsk18);
    }

    // API Such request
    protected class MyThread extends Thread {
        @Override
        public void run() {
            try {
                ApiService apiService = new ApiService();
                // API Request wird aufgerufen
                Intent intent = new Intent(SearchInputActivity.this, SearchResultActivity.class);
                intent.putExtra("origin", getString(R.string.search_resutls_score_title) + movie_title + "'");
                intent.putExtra("last_activity", "search");
                startActivity(intent);
            } catch (Exception e) {
                Log.e(getString(R.string.app_name), getString(R.string.exception), e);
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