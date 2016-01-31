package com.androidapps.project.popularmovies.model;


public class Trailer {


    private String id;
    private String key;
    private String name;

    public Trailer() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    // this is essential when we ckeck for equality and contains method in Arraylist
    @Override
    public boolean equals(Object obj) {
        return (this.id.equals(((Trailer) obj).id));
    }


}
