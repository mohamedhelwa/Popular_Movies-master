package com.androidapps.project.popularmovies.model;


public class Review {

    private String id;
    private String reviewrName;
    private String content;
    private String url;




    public Review() {

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReviewrName() {
        return reviewrName;
    }

    public void setReviewrName(String reviewrName) {
        this.reviewrName = reviewrName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // this is essential when we ckeck for equality and contains method in Arraylist
    @Override
    public boolean equals(Object obj) {
        return (this.id.equals(((Review) obj).id));

    }

}
