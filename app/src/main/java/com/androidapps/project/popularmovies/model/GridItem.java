package com.androidapps.project.popularmovies.model;


public class GridItem {

    private int id;
    private String PosterUrl;

    public  GridItem(){

    }

    public int getId() {return id;}

    public void setId(int id) {
        this.id = id;
    }

    public String getPosterUrl() {
        return PosterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        PosterUrl = posterUrl;
    }

}
