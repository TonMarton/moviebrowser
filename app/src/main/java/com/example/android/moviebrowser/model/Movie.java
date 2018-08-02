package com.example.android.moviebrowser.model;

public class Movie {

    private int id;
    private String title;
    private String posterPath;
    private String budget;

    public Movie(int id,String title, String posterPath) {
        this.id = id;
        this.title = title;
        this.posterPath = "https://image.tmdb.org/t/p/w500" + posterPath;
    }

    public String getPosterPath() {
        return this.posterPath;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return this.id;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }
}

