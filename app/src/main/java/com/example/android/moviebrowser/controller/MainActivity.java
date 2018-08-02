package com.example.android.moviebrowser.controller;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.moviebrowser.R;
import com.example.android.moviebrowser.model.Movie;
import com.example.android.moviebrowser.model.MovieAdapter;
import com.example.android.moviebrowser.model.Networking;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private List<Movie> movies;
    private EditText searchEditText;

    private boolean isBudgetQuery = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // it only works with enter for now
        searchEditText =findViewById(R.id.search_et);
        searchEditText.setImeActionLabel("Search", KeyEvent.KEYCODE_ENTER);
        searchEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String text = ((TextView)v).getText().toString();
                    isBudgetQuery = false;
                    makeMovieQuery(text);
                    return true;
                }
                return false;
            }
        });

        recyclerView = findViewById(R.id.main_rv);
        GridLayoutManager lm = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(lm);
        recyclerView.setHasFixedSize(true);
        adapter = new MovieAdapter(this);
        recyclerView.setAdapter(adapter);
        }

    private void makeMovieQuery(String search) {
        URL url = Networking.buildUrl(search);
        new MovieQueryTask().execute(url);
    }

    private void makeBudgetQuery() {
        List<URL> urls = new ArrayList<>();
        for (Movie movie : movies) {
            urls.add(Networking.buildMovieUrl(movie.getId()));
        }
        URL[] urlArray = urls.toArray(new URL[urls.size()]);
        isBudgetQuery = true;
        new MovieQueryTask().execute(urlArray);
    }

    private void addBudgetsToMovies(List<Integer> budgets) {
        for (int i = 0; i < movies.size(); i++) {
            int budget = budgets.get(i);
            movies.get(i).setBudget(Integer.toString(budget));
        }
        addToAdapter();
    }

    private void addToAdapter() {
        adapter.addNewItems(movies);
        adapter.notifyDataSetChanged();
    }

    // I didn't have time for fixing this
    private class MovieQueryTask extends AsyncTask<URL, Void, String[]> {

        @Override
        protected String[] doInBackground(URL... params) {
            String movieSearchResults = null;
            if (isBudgetQuery) {
                List<String> results = new ArrayList<>();
                for (URL url : params) {
                    try {
                        results.add(Networking.getResponseFromHttpUrl(url));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                String[] resultsArray = results.toArray(new String[results.size()]);
                return resultsArray;
            } else {
                URL searchUrl = params[0];
                try {
                    movieSearchResults = Networking.getResponseFromHttpUrl(searchUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return new String[]{movieSearchResults};
            }
        }

        @Override
        protected void onPostExecute(String[] movieSearchResults) {
            if (movieSearchResults != null) {
                if (isBudgetQuery) {
                    List<Integer> budgets = new ArrayList<>();
                    for (String result : movieSearchResults) {
                        try {
                            JSONObject json = new JSONObject(result);
                            int budget = json.getInt("budget");
                            budgets.add(budget);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    isBudgetQuery = false;
                    MainActivity.this.addBudgetsToMovies(budgets);
                } else {
                    List<Movie> movies = new ArrayList<Movie>();
                    try {
                        movies = parseJSON(movieSearchResults[0]);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (movies != null && movies.size() != 0) {
                        MainActivity.this.movies = movies;
                        MainActivity.this.makeBudgetQuery();
                    }
                }
            }
        }

        private List<Movie> parseJSON(String response) throws JSONException {
            List<Movie> movies = new ArrayList<Movie>();
            JSONObject json = new JSONObject(response);
            JSONArray results = json.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject result = results.getJSONObject(i);
                int id = result.getInt("id");
                String title = result.getString("title");
                String path = result.getString("poster_path");
                movies.add(new Movie( id,title ,path));
            }
            return movies;
        }
    }
}
