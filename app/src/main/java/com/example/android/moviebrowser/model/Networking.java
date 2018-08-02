package com.example.android.moviebrowser.model;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class Networking {

    private final static String SEARCH_BASE_URL = "https://api.themoviedb.org/3/search/movie";
    private final static String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie";
    private final static String API_KEY = "use_your_own";
    private final static String LANGUAGE = "en-US";
    private final static String PAGE_NUMBER = "1";
    private final static String INCLUDE_ADULT = "false";


    public static URL buildUrl(String query) {
        Uri builtUri = Uri.parse(SEARCH_BASE_URL).buildUpon()
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("language", LANGUAGE)
                .appendQueryParameter("query", query)
                .appendQueryParameter("page", PAGE_NUMBER)
                .appendQueryParameter("include_adult", INCLUDE_ADULT)
                .build();
        URL url = null;
        try {
            Log.d("url", builtUri.toString());
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildMovieUrl(int id) {
        String idString = Integer.toString(id);
        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(idString)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("language", LANGUAGE)
                .build();

        URL url = null;
        Log.d("movieUrl", builtUri.toString());
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}