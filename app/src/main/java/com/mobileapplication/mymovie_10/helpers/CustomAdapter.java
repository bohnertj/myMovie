package com.mobileapplication.mymovie_10.helpers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.mobileapplication.mymovie_10.Activities.SearchResutlDetailsActivity;
import com.mobileapplication.mymovie_10.R;
import com.mobileapplication.mymovie_10.db.MovieDAO;
import com.mobileapplication.mymovie_10.db.MyMovieDatabase;

import java.util.ArrayList;
import java.util.Collections;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private final Context context;
    private final Activity activity;
    private final ArrayList<Movie> movieList;
    private final ArrayList<Movie> favouriteList;
    private final ApiService apiService = new ApiService();
    private final String[] jsonData;
    private final String searchText;
    private MyMovieDatabase database = null;
    private final MovieDAO movieDAO;
    private final boolean fromMain;
    private String[] favouriteString;

    // Adapter funktioniert als Movie Liste -> Scrollbares Element auf der Search Results Activity
    public CustomAdapter(Activity activity, Context context, ArrayList<Movie> movieList, String[] jsonData, String searchText, boolean fromMain) {
        this.activity = activity;
        this.context = context;
        this.movieList = movieList;
        this.jsonData = jsonData;
        this.searchText = searchText;
        this.fromMain = fromMain;
        database = MyMovieDatabase.getSingletonInstance(context);
        movieDAO = database.movieDao();
        favouriteList = Movie.parseInMovieList(movieDAO.getAllMovies());
        updateFavourites();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.display_row, parent, false);
        return new MyViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        // Daten für einen Eintrag im Adapter gesetzt
        holder.movie_title.setText(movieList.get(position).getMovie_title());
        String date = "--";
        try {
            date = movieList.get(position).getMovie_releaseDate().substring(6, 10);
        } catch (Exception e) {
            Log.e("MyMovie", "exception", e);
        }
        holder.movie_release.setText("Veröffentlichung: " + date);
        holder.movie_rating.setText("Bewertung: " + movieList.get(position).getMovie_vote() + "/10.0");
        holder.movie_favourite.setText(favouriteString[position]);

        // Film Cover laden
        apiService.setMovieImg(movieList.get(position).getMovie_img(), context, holder.movie_cover);

        //Recyclerview onClickListener springt zur Detailansicht für einen Film und Übergibt Daten
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SearchResutlDetailsActivity.class);
                intent.putExtra("movie_name", movieList.get(position).getMovie_title());
                intent.putExtra("movie_score", movieList.get(position).getMovie_vote());
                intent.putExtra("movie_release", movieList.get(position).getMovie_releaseDate());
                intent.putExtra("movie_cover", movieList.get(position).getMovie_img());
                intent.putExtra("movie_description", movieList.get(position).getMovie_description());
                intent.putExtra("movie_age", movieList.get(position).getMovie_ageRating());
                intent.putExtra("movie_isFavourite", movieList.get(position).getMovie_favourite());
                intent.putExtra("movie_id", movieList.get(position).getId());
                intent.putExtra("api_result", jsonData);
                intent.putExtra("api_search_text", searchText);
                if (fromMain) {
                    intent.putExtra("last_activity", "main");
                } else {
                    intent.putExtra("last_activity", "search");
                }
                activity.startActivityForResult(intent, 1);
            }
        });
    }

    // Liefert Anzahl der Items Im Adapter
    @Override
    public int getItemCount() {
        return movieList.size();
    }

    // Gleicht Inhalt des Adapters mit den Favoriten ab und makiert diese
    private void updateFavourites() {
        favouriteString = new String[movieList.size()];
        for (int i = 0; i < movieList.size(); i++) {
            if (favouriteList.contains(movieList.get(i))) {
                movieList.get(i).setMovie_favourite(true);
                favouriteString[i] = "Favorit";
            } else {
                favouriteString[i] = "";
            }
        }
    }

    // Sortiert Adapter Daten aufsteigend/absteigend und nach einem Element
    public CustomAdapter sortData(boolean ascending, int sortingObject) {
        if (sortingObject == 0) {
            if (ascending) {
                Collections.sort(movieList, new Movie.SortName());
            } else {
                Collections.sort(movieList, Collections.reverseOrder(new Movie.SortName()));
            }
        } else if (sortingObject == 1) {
            if (ascending) {
                Collections.sort(movieList, new Movie.SortRelease());
            } else {
                Collections.sort(movieList, Collections.reverseOrder(new Movie.SortRelease()));
            }
        } else {
            if (ascending) {
                Collections.sort(movieList, new Movie.SortVoting());
            } else {
                Collections.sort(movieList, Collections.reverseOrder(new Movie.SortVoting()));
            }
        }
        return new CustomAdapter(activity, context, movieList, jsonData, searchText, fromMain);
    }

    // Initialisierung der der Elmente eines Listenelement
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView movie_title, movie_release, movie_rating, movie_favourite;
        ImageView movie_cover;
        LinearLayout mainLayout;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            movie_cover = itemView.findViewById(R.id.movieCover);
            movie_title = itemView.findViewById(R.id.movieTitle);
            movie_release = itemView.findViewById(R.id.movieRelease);
            movie_rating = itemView.findViewById(R.id.movieRating);
            movie_favourite = itemView.findViewById(R.id.movieFavourite);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            //Animate Recyclerview
            Animation translate_anim = AnimationUtils.loadAnimation(context, R.anim.translate_anim);
            mainLayout.setAnimation(translate_anim);
        }
    }
}
