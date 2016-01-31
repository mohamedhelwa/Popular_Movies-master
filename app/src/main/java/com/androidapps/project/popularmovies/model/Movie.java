package com.androidapps.project.popularmovies.model;


public class Movie {

    private int id;
    private String posterUrl;
    private String description;
    private String title;
    private String year;
    private long duration;
    private long favorite;
    private long rate;

    public Movie() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        try {


            if (year != null) {
                String yearReturned = year;
                String modefiedYear = yearReturned.substring(0, yearReturned.indexOf('-'));
                this.year = modefiedYear;
            }
        } catch (Exception e) {
            this.year="null";
        }
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getFavorite() {
        return favorite;
    }

    public void setFavorite(long favorite) {
        this.favorite = favorite;
    }

    public long getRate() {
        return rate;
    }

    public void setRate(long rate) {
        this.rate = rate;
    }


    @Override
    public boolean equals(Object o) {
        if ((o instanceof Movie) && (((Movie) o).getId() == this.id)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
