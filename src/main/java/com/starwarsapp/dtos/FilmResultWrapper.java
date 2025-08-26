package com.starwarsapp.dtos;

public class FilmResultWrapper {
    private FilmDTO properties;
    private String uid;

    public FilmDTO getProperties() {
        return properties;
    }

    public void setProperties(FilmDTO properties) {
        this.properties = properties;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
