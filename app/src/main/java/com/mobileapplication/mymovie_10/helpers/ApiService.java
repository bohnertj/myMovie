package com.mobileapplication.mymovie_10.helpers;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mobileapplication.mymovie_10.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ApiService {
    private final String baseDomain = "https://api.themoviedb.org/3";
    private final String search = "/search/movie";
    private final String topRated = "/movie/top_rated";
    private final String popular = "/movie/popular";
    private final String findById = "/movie/";
    private final String apiKey = "b178ee5892b8a7564cb69fd25ab4fb04";

    // API Request für Suche
    public String[] getMovies(String movie_name, int movie_year, String movie_language, boolean movie_fsk18, int hitAmound) throws IOException {
        String url = baseDomain + search + "?api_key=" + apiKey + "&language=" + movie_language + "&query=" + movie_name + "&page=1&include_adult=" + movie_fsk18 + "&primary_release_year=" + movie_year;
        return getAPIData(url, hitAmound);
    }

    // API Request für am besten bewertete Filme
    public String[] getTopRatedMovies(String movie_language, String movie_region, int hitAmound) throws IOException {
        String url = baseDomain + topRated + "?api_key=" + apiKey + "&language=" + movie_language + "&region=" + movie_region;
        return getAPIData(url, hitAmound);
    }

    // API Request für derzeit beliebte Filme
    public String[] getNowPopularMovies(String movie_language, String movie_region, int hitAmound) throws IOException {
        String url = baseDomain + popular + "?api_key=" + apiKey + "&language=" + movie_language + "&region=" + movie_region;
        return getAPIData(url, hitAmound);
    }

    // API Request für Suche nach Filmen über ID
    public String getFindMovieById(int id, String movie_language) throws IOException {
        return getData(new URL(baseDomain + findById + id + "?api_key=" + apiKey + "&language=" + movie_language));
    }

    // API Request für landen des Film Covers
    public void setMovieImg(String name, Context context, ImageView image) {
        try {
            URL imageurl = new URL("https://image.tmdb.org/t/p/original" + name);
            if (!name.equals("null")) {
                Glide.with(context).load(imageurl).into(image);
            } else {
                image.setImageResource(R.drawable.unknown_image);
            }
        } catch (MalformedURLException e) {
            Log.e("MyMovie","Exception",e);
        }
    }

    // Führt eigentlichen API Rquest durch (Internetzugriff)
    private String getData(URL url) throws IOException {
        HttpURLConnection conn = null;
        conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        String responseResult = "";
        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            String errorMessage = "HTTP-Fehler: " + conn.getResponseMessage();
            throw new IOException(errorMessage);
        } else {
            InputStream is = conn.getInputStream();
            InputStreamReader ris = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(ris);
            // JSON-Dokument zeilenweise einlesen
            String row = "";
            while ((row = reader.readLine()) != null) {
                responseResult += row;
            }
        }
        return responseResult;
    }

    // Lädt beliebig viele pages (wiederholter API request)
    private String[] getAPIData(String url, int hitAmound) throws IOException {
        int pageMaxAmount;
        if (hitAmound % 20 == 0) {
            pageMaxAmount = hitAmound / 20;
        } else {
            pageMaxAmount = hitAmound / 20 + 1;
        }
        int pageLength = 1;
        String jsonBufferString = getData(new URL(url + "&page=" + pageLength));
        try {
            pageLength = getPageAmount(jsonBufferString);
        } catch (JSONException e) {
            Log.e("MyMovie","Exception",e);
        }
        if (pageMaxAmount == 0) {
        } else if (pageLength > pageMaxAmount) {
            pageLength = pageMaxAmount;
        } else if (pageLength == 0) {
            pageLength = 1;
        }
        String[] jsonArray = new String[pageLength];
        jsonArray[0] = jsonBufferString;
        for (int i = 2; i <= pageLength; i++) {
            jsonBufferString = getData(new URL(url + "&page=" + i));
            jsonArray[i - 1] = jsonBufferString;
        }
        return jsonArray;
    }

    // Liefert die Anzahl der Ergebnis Pages für API Request
    private int getPageAmount(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        return jsonObject.getInt("total_pages");
    }

    // Überträgt alle Daten aus String Array (Movies) in Array List (Movies)
    public ArrayList<Movie> parseJSON(String[] jsonString) throws JSONException {
        ArrayList<Movie> bufferListe = new ArrayList<Movie>();
        for (int i = 0; i < jsonString.length; i++) {
            bufferListe.addAll(parseSingelJSON(jsonString[i]));
        }
        return bufferListe;
    }

    // Überträgt Daten aus eingabe String in Array List
    private ArrayList<Movie> parseSingelJSON(String jsonString) throws JSONException {
        ArrayList<Movie> bufferList = new ArrayList<Movie>();
        Movie bufferMovie;
        // eigentliches Parsen der JSON-Datei
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray array = jsonObject.getJSONArray("results");
        for (int i = 0; i < array.length(); i++) {
            String title = array.getJSONObject(i).getString("title");
            int id = array.getJSONObject(i).getInt("id");
            String release_date;
            try {
                String buffer_date = array.getJSONObject(i).getString("release_date");
                String buffer_day, buffer_month, buffer_year;
                buffer_day = buffer_date.substring(8, 10);
                buffer_month = buffer_date.substring(5, 7);
                buffer_year = buffer_date.substring(0, 4);
                release_date = buffer_day + "." + buffer_month + "." + buffer_year;
            } catch (Exception e) {
                Log.e("MyMovie","Exception",e);
                release_date = "--";
            }
            String description = array.getJSONObject(i).getString("overview");
            String img = array.getJSONObject(i).getString("backdrop_path");
            double vote = array.getJSONObject(i).getDouble("vote_average");
            boolean age = array.getJSONObject(i).getBoolean("adult");
            bufferMovie = new Movie(id, title, release_date, description, img, age, vote, false);
            bufferList.add(bufferMovie);
        }
        return bufferList;
    }

    // Überträgt Daten aus JSON Dokument ein Movie Objekt
    public Movie pareJSON(String jsonString) throws JSONException {
        Movie bufferMovie;
        JSONObject jsonObject = new JSONObject(jsonString);
        String title = jsonObject.getString("title");
        int id = jsonObject.getInt("id");
        String release_date;
        try {
            String buffer_date = jsonObject.getString("release_date");
            String buffer_day, buffer_month, buffer_year;
            buffer_day = buffer_date.substring(8, 10);
            buffer_month = buffer_date.substring(5, 7);
            buffer_year = buffer_date.substring(0, 4);
            release_date = buffer_day + "." + buffer_month + "." + buffer_year;
        } catch (Exception e) {
            release_date = "--";
        }
        String description = jsonObject.getString("overview");
        String img = jsonObject.getString("backdrop_path");
        double vote = jsonObject.getDouble("vote_average");
        Boolean age = jsonObject.getBoolean("adult");
        bufferMovie = new Movie(id, title, release_date, description, img, age, vote, false);
        return bufferMovie;
    }
}
