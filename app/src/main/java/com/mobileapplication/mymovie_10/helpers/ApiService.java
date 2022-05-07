package com.mobileapplication.mymovie_10.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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


}
